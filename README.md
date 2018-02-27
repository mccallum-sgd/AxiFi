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

All data is stored locally in an SQLite database, which can be optionally encrypted via AES-256 ([more info](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard)) with a separate password .

## Releases
Releases are hosted on this repository. See the [releases page](https://github.com/mccallum-sgd/AxiFi/releases).

___

## Developers
This project uses the  [e(fx)clipse](https://www.eclipse.org/efxclipse/index.html) Eclipse extension, and subsequently [Apache Ant](https://ant.apache.org/), to generate various installers for Windows (.exe), MacOS (.dmg), and Linux (.deb).



### Testing
Can be run as a normal Java application. The main method is in **application.Main**. There is also some unit testing for classes that need it like **DatabaseKit**, **Security**, and **Settings**.

### JCE
This project requires the Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files to allow for the AES-256 encryption key length. If you do not install the JCE, the program will be unable to encrypt the database file and you will get an `InvalidKeyException: Invalid AES key length`.
- [JCE 8](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
- [JCE 7](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html)
- [JCE 6](http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html)

After downloading, extract the `local_policy` and `US_export_policy` JARs to your JDK 8 installation folder under `jre/lib/security/policy`.

### Installing Eclipse

I've had many problems with getting e(fx)clipse to install with various methods on [Eclipse Oxygen](https://www.eclipse.org/oxygen/). You can try it on Oxygen, but if it doesn't work install [Eclipse Neon](http://www.eclipse.org/downloads/packages/release/Neon/3).

### Installing e(fx)clipse

The method I've had most success with is [here](https://www.eclipse.org/efxclipse/install.html#for-the-ambitious). 
- Alternatively, try installing e(fx)clipse directly from the Eclipse Marketplace (**Help > Eclipse Marketplace > Search for "e(fx)clipse"**), or try the other methods list in the link above.

### Deployment with e(fx)clipse

After installing e(fx)clipse, the installers can be built by opening **build.fxbuild** and clicking "_Generate ant build.xml and run_" in the **Building_Exporting** section on the top right (or running the existing Ant build.xml in the build directory).

This will generate the installers in **\build\deploy**. Installers must be built from their respective OS environments.
