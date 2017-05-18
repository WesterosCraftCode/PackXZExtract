This is a utility library to allow us to extract jar files from .pack.xz format.

The program can take three commandline arguments:

* ```-packxz filepath1.pack.xz,filepath2.pack.xz,...``` - Decompress and unpack the file(s) specified.
* ```-xz filepath1.xz,filepath2.xz,...``` - Decompress the file(s) specified.
* ```-pack filepath1.pack,filepath2.pack,...``` - Unpack the file(s) specified.

You can specify multiple files by separating the paths with `,` commas.