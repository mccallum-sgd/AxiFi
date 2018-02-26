# AxiFi (by CyberDyne)

A financial tracking program. Tracks:
- Admin account (not implemented -- currently hard-coded)
    - first name, last name
    - username, password
- User accounts
    - first name, last name
    - username, password (not implemented)
    - balance
- Transactions
    - date
    - amount
    - description
    - fees (currently hard-coded)
    - predefined or user-defined id

All data is stored locally in an SQLite database, which can be optionally encrypted via AES-256 with a separate password (currently default).

## Releases
Releases are hosted on this repository. See the [releases page](https://github.com/mccallum-sgd/AxiFi/releases).

## Developers

### Testing
Can be run as a normal Java application in eclipse. The main method is in **MainApp.java**.

### Deployment
This project uses the  [e(fx)clipse](https://www.eclipse.org/efxclipse/index.html) Eclipse extension, and subsequently [Apache Ant](https://ant.apache.org/), to generate various installers for Windows (.exe), MacOS (.dmg), and Linux (.deb).

After installing e(fx)clipse, the installer can be built by opening **build.fxbuild** and clicking "_Generate ant build.xml and run_" in the **Building_Exporting** section on the top right (or running the existing Ant build.xml in the build directory).

This will generate the installers in **\build\deploy**.
