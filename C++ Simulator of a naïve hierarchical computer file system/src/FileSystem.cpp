
#include "../include/FileSystem.h"
#include "../include/GlobalVariables.h"


//Constructor
FileSystem::FileSystem():rootDirectory(),workingDirectory()
{
    rootDirectory = new Directory("~", nullptr );
    workingDirectory = rootDirectory;
}


//Destructor
FileSystem::~FileSystem()
{
    delete rootDirectory;
    if(verbose == 1 || verbose == 3)
        cout<< "FileSystem::~FileSystem()" << endl;

}

//Operator =
FileSystem& FileSystem:: operator=(FileSystem& other)  //copy the content of other folder but NOT parent
{
    workingDirectory = other.workingDirectory;
    rootDirectory = other.rootDirectory;

    if(verbose == 1 || verbose == 3)
        cout<< "FileSystem& FileSystem:: operator=(FileSystem& other)" << endl;

    return *this;
}

//Copy Constructor
FileSystem:: FileSystem(FileSystem& other): FileSystem()
{
    rootDirectory =other.rootDirectory;
    workingDirectory =other.workingDirectory;
    if(verbose == 1 || verbose == 3)
        cout<< "FileSystem:: FileSystem(FileSystem& other)" << endl;

}

//Move constructor
FileSystem:: FileSystem (FileSystem && other):rootDirectory(&other.getRootDirectory()), workingDirectory(&other.getRootDirectory())
{

    other.workingDirectory = nullptr;
    other.rootDirectory = nullptr;

    if(verbose == 1 || verbose == 3)
        cout<< "FileSystem:: FileSystem (FileSystem && other)" << endl;
}

//Move assignment
FileSystem& FileSystem:: operator=(FileSystem && other)
{
    rootDirectory =other.rootDirectory;
    workingDirectory =other.workingDirectory;

    if(verbose == 1 || verbose == 3)
        cout<< "FileSystem& FileSystem:: operator=(FileSystem && other)" << endl;

    return *this;
}



//Methods
Directory& FileSystem::getRootDirectory() const
{
    return *rootDirectory;
}

Directory& FileSystem::getWorkingDirectory() const
{
    return *workingDirectory;
}

void FileSystem::setWorkingDirectory(Directory *newWorkingDirectory)
{
    workingDirectory = newWorkingDirectory;
}