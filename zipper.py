import os
import zipfile

with zipfile.ZipFile("b2220356127.zip", "w",compression=zipfile.ZIP_STORED) as zip:
    for root, dirs, files in os.walk("."):
        for file in files:
            if file.endswith(".java"):
                zip.write(os.path.join(root, file))
