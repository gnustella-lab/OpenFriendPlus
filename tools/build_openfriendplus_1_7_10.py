#!/usr/bin/env python3
"""Build the Forge 1.7.10 OpenFriendPlus jar from the original OpenFriend jar.

The original 1.7.10 artifact is only available as a compiled jar in this
workspace. This script applies a narrow, reproducible binary transformation:
class constant-pool strings are rewritten, jar entries are renamed, metadata is
updated, and bundled native helper binaries are kept intact.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import struct
import sys
import zipfile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DEFAULT_INPUT = ROOT / "OpenFriend original" / "OpenFriend-forge-1.7.10.jar"
DEFAULT_OUTPUT = ROOT / "build" / "libs" / "OpenFriendPlus-forge-1.7.10.jar"

PLUS_VERSION = "1.0.9-plus"
PACKAGE_FROM = "jp/zpw/openfriend"
PACKAGE_TO = "dev/gnustella/openfriendplus17"
ORIGINAL_RELEASES_URL = "https://api.github.com/repos/zerozshare/OpenFriendMod/releases?per_page=100"
PLUS_RELEASES_URL = "https://api.github.com/repos/gnustella-lab/OpenFriendPlus/releases?per_page=100"


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def transform_utf8(text: str) -> str:
    if text == ORIGINAL_RELEASES_URL:
        return PLUS_RELEASES_URL
    if text == "openfriend":
        return "openfriendplus"
    if text == "1.0.9":
        return PLUS_VERSION

    replacements = (
        (PACKAGE_FROM, PACKAGE_TO),
        ("jp.zpw.openfriend", "dev.gnustella.openfriendplus17"),
        ("jp$zpw$openfriend", "dev$gnustella$openfriendplus17"),
        ("textures/gui/openfriend_icon.png", "textures/gui/openfriendplus_icon.png"),
        ("openfriend-update-check", "openfriendplus-update-check"),
        ("openfriend-", "openfriendplus-"),
        ("openfriend/", "openfriendplus/"),
        ("OpenFriend", "OpenFriendPlus"),
    )

    out = text
    for old, new in replacements:
        out = out.replace(old, new)
    return out


def copy_fixed(data: bytes, offset: int, out: bytearray, size: int) -> int:
    out.extend(data[offset : offset + size])
    return offset + size


def patch_class(data: bytes) -> tuple[bytes, int]:
    if not data.startswith(b"\xca\xfe\xba\xbe"):
        return data, 0

    cp_count = struct.unpack_from(">H", data, 8)[0]
    offset = 10
    out = bytearray(data[:10])
    changes = 0
    index = 1

    while index < cp_count:
        tag = data[offset]
        out.append(tag)
        offset += 1

        if tag == 1:
            length = struct.unpack_from(">H", data, offset)[0]
            offset += 2
            raw = data[offset : offset + length]
            offset += length
            try:
                text = raw.decode("utf-8")
            except UnicodeDecodeError:
                out.extend(struct.pack(">H", length))
                out.extend(raw)
                index += 1
                continue

            new_raw = transform_utf8(text).encode("utf-8")
            if len(new_raw) > 0xFFFF:
                raise ValueError("patched constant-pool string is too long")
            out.extend(struct.pack(">H", len(new_raw)))
            out.extend(new_raw)
            if new_raw != raw:
                changes += 1
        elif tag in (3, 4):
            offset = copy_fixed(data, offset, out, 4)
        elif tag in (5, 6):
            offset = copy_fixed(data, offset, out, 8)
            index += 1
        elif tag in (7, 8, 16, 19, 20):
            offset = copy_fixed(data, offset, out, 2)
        elif tag in (9, 10, 11, 12, 18):
            offset = copy_fixed(data, offset, out, 4)
        elif tag == 15:
            offset = copy_fixed(data, offset, out, 3)
        else:
            raise ValueError(f"unsupported class constant-pool tag {tag} at index {index}")

        index += 1

    out.extend(data[offset:])
    return bytes(out), changes


def transform_entry_name(name: str) -> str:
    out = name
    if out.startswith(PACKAGE_FROM + "/") or out == PACKAGE_FROM + "/":
        out = PACKAGE_TO + out[len(PACKAGE_FROM) :]
    elif out.startswith("assets/openfriend/") or out == "assets/openfriend/":
        out = "assets/openfriendplus" + out[len("assets/openfriend") :]
    elif out.startswith("openfriend/") or out == "openfriend/":
        out = "openfriendplus" + out[len("openfriend") :]

    out = out.replace("OpenFriend", "OpenFriendPlus")
    out = out.replace("openfriend_icon.png", "openfriendplus_icon.png")
    out = out.replace("openfriend-", "openfriendplus-")
    return out


def plus_mcmod_info() -> bytes:
    metadata = [
        {
            "modid": "openfriendplus",
            "name": "OpenFriendPlus",
            "description": (
                "Enhanced OpenFriend fork for Forge 1.7.10. Bridges the Minecraft "
                "Java friends list to legacy clients with isolated Plus branding, "
                "resources, data directory, update channel, and compatibility notes."
            ),
            "version": PLUS_VERSION,
            "mcversion": "1.7.10",
            "url": "https://github.com/gnustella-lab/OpenFriendPlus",
            "authorList": ["gnustella-lab", "ZSHARE - original OpenFriend author"],
        }
    ]
    return (json.dumps(metadata, indent=2, ensure_ascii=True) + "\n").encode("utf-8")


def plus_note(original_hash: str) -> bytes:
    text = f"""OpenFriendPlus - Forge 1.7.10

This jar is generated from OpenFriend 1.0.9 for Forge 1.7.10.

Applied Plus changes:
- mod id changed from openfriend to openfriendplus
- display name changed from OpenFriend to OpenFriendPlus
- Java package relocated from jp.zpw.openfriend to dev.gnustella.openfriendplus17
- asset namespace and icon path changed to openfriendplus
- bundled helper resource root changed to openfriendplus
- update checker points to https://github.com/gnustella-lab/OpenFriendPlus
- data directory is isolated under openfriendplus
- internal compatibility note added

Compatibility:
- Loader: Forge
- Minecraft: 1.7.10
- Java: 8 or newer
- Side: client only
- Single player: works in single player worlds
- Server: compatible with standard servers; do not install as a dedicated server mod

Original jar SHA-256: {original_hash}
Generated version: {PLUS_VERSION}
"""
    return text.encode("utf-8")


def zip_info_for(source: zipfile.ZipInfo, filename: str) -> zipfile.ZipInfo:
    info = zipfile.ZipInfo(filename=filename, date_time=source.date_time)
    info.comment = source.comment
    info.extra = source.extra
    info.internal_attr = source.internal_attr
    info.external_attr = source.external_attr
    info.create_system = source.create_system
    info.compress_type = zipfile.ZIP_STORED if filename.endswith("/") else zipfile.ZIP_DEFLATED
    return info


def add_generated_entry(
    jar: zipfile.ZipFile,
    filename: str,
    data: bytes,
    names: set[str],
    original: zipfile.ZipInfo,
) -> None:
    if filename in names:
        raise ValueError(f"duplicate output entry: {filename}")
    info = zip_info_for(original, filename)
    info.external_attr = 0o644 << 16
    jar.writestr(info, data)
    names.add(filename)


def build(input_path: Path, output_path: Path) -> dict[str, int | str]:
    if not input_path.is_file():
        raise FileNotFoundError(f"input jar not found: {input_path}")

    original_hash = sha256(input_path)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    temp_path = output_path.with_suffix(output_path.suffix + ".tmp")

    stats: dict[str, int | str] = {
        "classes_patched": 0,
        "constants_patched": 0,
        "entries_renamed": 0,
        "resources_added": 1,
        "original_sha256": original_hash,
    }

    names: set[str] = set()
    mcmod_source_info: zipfile.ZipInfo | None = None

    with zipfile.ZipFile(input_path, "r") as source, zipfile.ZipFile(temp_path, "w") as target:
        for source_info in source.infolist():
            old_name = source_info.filename
            if old_name in {"jp/", "jp/zpw/"}:
                continue
            new_name = transform_entry_name(old_name)
            data = source.read(source_info) if not old_name.endswith("/") else b""

            if old_name == "mcmod.info":
                data = plus_mcmod_info()
                mcmod_source_info = source_info
            elif old_name.endswith(".class"):
                data, changes = patch_class(data)
                if changes:
                    stats["classes_patched"] = int(stats["classes_patched"]) + 1
                    stats["constants_patched"] = int(stats["constants_patched"]) + changes

            if old_name != new_name:
                stats["entries_renamed"] = int(stats["entries_renamed"]) + 1
            if new_name in names:
                raise ValueError(f"duplicate output entry: {new_name}")

            target.writestr(zip_info_for(source_info, new_name), data)
            names.add(new_name)

        if mcmod_source_info is None:
            raise ValueError("mcmod.info not found in source jar")
        add_generated_entry(
            target,
            "OPENFRIENDPLUS-1.7.10.txt",
            plus_note(original_hash),
            names,
            mcmod_source_info,
        )

    temp_path.replace(output_path)
    stats["output"] = str(output_path)
    return stats


def parse_args(argv: list[str]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--input", type=Path, default=DEFAULT_INPUT, help="original OpenFriend jar")
    parser.add_argument("--output", type=Path, default=DEFAULT_OUTPUT, help="generated OpenFriendPlus jar")
    return parser.parse_args(argv)


def main(argv: list[str]) -> int:
    args = parse_args(argv)
    stats = build(args.input, args.output)
    for key, value in stats.items():
        print(f"{key}: {value}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
