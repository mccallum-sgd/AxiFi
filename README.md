# AxiFi (by CyberDyne)

<kbd><img src="src/resources/axifi-logo_small.png?raw=true" alt="AxiFi Logo: dotted circle with books next to 'AxiFi'"/></kbd>

___
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

___

## Developers
This project uses the  [e(fx)clipse](https://www.eclipse.org/efxclipse/index.html) Eclipse extension, and subsequently [Apache Ant](https://ant.apache.org/), to generate various installers for Windows (.exe), MacOS (.dmg), and Linux (.deb).

### Testing
Can be run as a normal Java application. The main method is in **application.Main**. There is also some unit testing for classes that need it like **DatabaseKit**, **Security**, and **Settings**.

## Installing Eclipse

I've had many problems with getting e(fx)clipse to install with various methods on Eclipse Oxygen (https://www.eclipse.org/oxygen/). You can try it on Oxygen, but if it doesn't work install [Eclipse Neon (4.6)](http://www.eclipse.org/downloads/packages/release/Neon/3).

## Installing e(fx)clipse

The method I've had most success with is [here](https://www.eclipse.org/efxclipse/install.html#for-the-ambitious). 
- If that installation fails for some reason, try installing e(fx)clipse directly from the Eclipse Marketplace (**Help > Eclipse Marketplace > Search for "e(fx)clipse"**).
- If that installation also fails, you can try the other methods listed in the original link above.
___

### Deployment

After installing e(fx)clipse, the installers can be built by opening **build.fxbuild** and clicking "_Generate ant build.xml and run_" in the **Building_Exporting** section on the top right (or running the existing Ant build.xml in the build directory).

This will generate the installers in **\build\deploy**. Only invited Cyberdyne members will be allowed to add releases to this repository.
