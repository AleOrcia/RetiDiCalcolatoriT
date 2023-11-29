struct Scan{
    char nomeDir[200];
    int soglia;
};

struct File_scan{
    int chars;
    int words;
    int lines;
};

struct Dir{
    char Directories[200];
};

struct Dir_scan{
    int numFile;
    Dir Directories[8];
};

program SCANPROG {
	version SCANVERS {
		File_scan FILE_SCAN(string) = 1;
        Dir_scan DIR_SCAN(Scan) = 2;
	} = 1;
} = 0x20000013;