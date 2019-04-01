
#include "../include/Commands.h"
#include "../include/GlobalVariables.h"
#include <cstddef>
#include <s9core.h>
////                                                      BaseCommand

//Constructor
BaseCommand::BaseCommand(string args):args(args){}
BaseCommand::~BaseCommand(){}
//Methods

string BaseCommand::getArgs()
{
    return args;
}




vector <string> BaseCommand::readPath(string input)
{
    vector <string> path ;

    size_t pos1 = input.find_first_of('/');
    size_t pos2;
    size_t lastSpace;
    int wordLength ;

    if(pos1 == input.length()-1) {
        path.push_back("~");
        return path;
    }

    // one directory path
    if(pos1 == string::npos)
    {
        lastSpace = input.rfind(' ');
        path.push_back(input.substr(lastSpace+1));
        return path;
    }


    // Absolute path
    if (input[pos1-1] == ' ') {
        path.push_back("~");
    }

    // Relative path
    else  {
        lastSpace = input.rfind(' ');
        wordLength = pos1-lastSpace-1;
        path.push_back(input.substr(lastSpace+1,wordLength));

    }



    if(pos1 == input.size()-1)
        return path;





    while (pos1 != string::npos)
    {
        pos2 = input.find_first_of('/', pos1+1);

        //last dir
        if(pos2 == string::npos) {
            path.push_back(input.substr(pos1 + 1));
            return path;
        }
        else
        {
            wordLength = pos2-pos1-1;
            path.push_back(input.substr(pos1+1, wordLength));
        }
        pos1=pos2;
  }
    return path;
}



Directory* BaseCommand::isLegalPath(FileSystem& fs, vector<string> path)
{
    Directory* currDir;
    bool legal = true;
    int startInx=0;


    if(path.size()==0)
        return nullptr;
    string currName = path[0];

    if(currName == "~" || currName == "/") {
        currDir = &fs.getRootDirectory();
        startInx++;
    }
    else
        currDir = &fs.getWorkingDirectory();


    for(size_t i = startInx ; i< path.size() && legal; i++)
    {
        if(path[i] == "..")
            currDir = currDir->getParent();


        else
            currDir = currDir->findChild(path[i]);


        if (currDir == nullptr)
            return nullptr;

    }

    return currDir;
}

void BaseCommand::buildPath(FileSystem & fs, vector<string> path)
{
    Directory* currDir;
    Directory* nextDir;
    int startInx=0;


    string currName = path[0];

    if(currName == "~") {
        currDir = &fs.getRootDirectory();
        startInx++;
    }
    else
        currDir = &fs.getWorkingDirectory();



    nextDir=currDir;

    for(size_t i = startInx ; i< path.size(); i++)
    {

        nextDir = currDir->findChild(path[i]);

        if((currDir->isFileExist(path[i]))!= nullptr)
        {
            cout << "The directory already exists" << endl;
            return;
        }

        if(nextDir == nullptr )
        {

            Directory * newDir = new Directory (path[i], currDir);
            currDir->addFile(newDir);
            currDir = newDir;
        }

        else
            currDir = nextDir;
    }
}

bool BaseCommand::isPossibleToDel(FileSystem & fs, Directory* dir)
{
    bool isLegal = true;
    Directory* curr = &fs.getWorkingDirectory();
    while(curr != nullptr && isLegal)
    {
        if (curr == dir)
            isLegal = false;
        else

            curr = curr->getParent();
    }
    return isLegal;
}

//-------------------------------------------------------------------------------------------------------------------


////                                                      PwdCommand

//Constructor
PwdCommand::PwdCommand(string args): BaseCommand(args){}
PwdCommand::~PwdCommand(){}
//Methods
void PwdCommand::execute(FileSystem & fs)
{
    cout << fs.getWorkingDirectory().getAbsolutePath() << endl;
}

string PwdCommand::toString()
{
    return "pwd";
}

////                                                      LsCommand

//Constructor
LsCommand::LsCommand(string args): BaseCommand(args){}
LsCommand::~LsCommand(){}
//Methods
void LsCommand::execute(FileSystem & fs)
{
    string inputCopy;
    if (getArgs() == "ls")
    {
        regularExecute(fs);
    }
    else
    {
        if (getArgs() == "ls -s")
        {
            sizeExecute(fs);
        }
        else                        //ls with path
        {
            vector<string> path = readPath(getArgs());
            Directory *newWorkingDir = isLegalPath(fs,path);
            if(newWorkingDir == nullptr)
            {
                cout<< "The system cannot find the path specified" << endl;
                return;
            }
            else
            {
                Directory *originalWorkingDir = &fs.getWorkingDirectory();
                fs.setWorkingDirectory(newWorkingDir);
                size_t sizeOrName = getArgs().find("-s");

                if(sizeOrName == string::npos)  //Sort by name
                    regularExecute(fs);
                else                            //Sort by size
                    sizeExecute(fs);

                fs.setWorkingDirectory(originalWorkingDir);

            }

        }
    }

}




void LsCommand::sizeExecute(FileSystem & fs)
{
    Directory& dir =  fs.getWorkingDirectory();
    dir.sortBySize();
    vector<BaseFile*> children = dir.getChildren();
    for(size_t i=0 ; i<children.size(); i++)
    {
        if(children[i]->isFile())
            cout << "FILE" "\t" ;
        else
            cout << "DIR" "\t";
        cout << children[i]->getName()<<"\t"<<children[i]->getSize()<<endl;

    }
}


void LsCommand::regularExecute(FileSystem & fs)
{
    Directory& dir =  fs.getWorkingDirectory();
    dir.sortByName();
    vector<BaseFile*> children = dir.getChildren();
    for(size_t i=0 ; i<children.size(); i++)
    {
        if(children[i]->isFile())
            cout << "FILE" "\t"  ;
        else
            cout << "DIR" "\t"  ;
        cout << children[i]->getName()<<"\t"<<children[i]->getSize()<<endl;

    }
}




string LsCommand::toString()
{
    return "ls";
}





////                                                      MkdirCommand


//Constructor
MkdirCommand::MkdirCommand(string args): BaseCommand(args){}
MkdirCommand::~MkdirCommand(){}
//Methods
void MkdirCommand::execute(FileSystem & fs)
{
    vector <string> path = readPath(getArgs());
    if (isLegalPath(fs,path) != nullptr)
    {
        cout << "The directory already exists"<<endl;
        return;
    }

    string isFile = path[path.size()-1];
    path.pop_back();
    Directory *aba = isLegalPath(fs,path);

    if(aba != nullptr && aba->isFileExist(isFile) != nullptr) {
        cout << "The directory already exists" << endl;
        return;
    }


    path.push_back(isFile);

    if(path.size() == 1&& path[0] != "~")
    {
        Directory* root =  &fs.getRootDirectory();
        if(root->isFileExist(isFile) != nullptr)
            return;
    }





    buildPath(fs,path);
}


string MkdirCommand::toString()
{
    return "mkdir";
}

////                                                       CdCommand

//Constructor
CdCommand::CdCommand(string args): BaseCommand(args){}
CdCommand::~CdCommand(){}
//Methods
void CdCommand::execute(FileSystem & fs)
{
    vector <string> path = readPath(getArgs());



    if((path.size()==1 ) && (path[0]==".." || getArgs() == "cd.."))
    {
        if(&fs.getRootDirectory() == &fs.getWorkingDirectory())
            return;

        fs.setWorkingDirectory(fs.getWorkingDirectory().getParent());
        return;
    }

    if((getArgs() == "cd/" || path[0]=="~" )&& ( path.size()==1 )) {
        fs.setWorkingDirectory(&fs.getRootDirectory());
        return;
    }



    Directory* target = isLegalPath(fs,path);

    if(target == nullptr) {
        cout << "The system cannot find the path specified" << endl;
        return;
    }

    else
        fs.setWorkingDirectory(target);

    return;
}



string CdCommand::toString()
{
    return "cd";
}


////                                                       MkfileCommand

//Constructor
MkfileCommand::MkfileCommand(string args): BaseCommand(args){}
MkfileCommand::~MkfileCommand(){}
//Methods
void MkfileCommand::execute(FileSystem & fs)
{
    size_t posOfLastSpace = getArgs().rfind(' ');

    string size = getArgs().substr(posOfLastSpace+1);

    int sizeOfFile = atoi (size.c_str());
    if(sizeOfFile == 0)
        if(size != "0")
            return;

    string readpath = getArgs().substr(0,getArgs().size() - size.size()-1);

    vector <string> path = readPath(readpath);
    string fileName = path[path.size()-1];

    path.pop_back();
    Directory* target;
    if(path.size() == 0)
        target = &fs.getWorkingDirectory();
    else
        target = isLegalPath(fs,path);


    if(target == nullptr)
    {
        cout << "The system cannot find the path specified" << endl;
        return;
    }

    if(target->isFileExist(fileName) != nullptr)
    {
        cout << "File already exists"<< endl;
        return;
    }
    if(target->findChild(fileName) != nullptr)
        return;


    target->addFile(new File(fileName,sizeOfFile));

    return;

}

string MkfileCommand::toString()
{
    return "mkfile";
}

////                                                        CpCommand

//Constructor
CpCommand::CpCommand(string args): BaseCommand(args){}
CpCommand::~CpCommand(){}
//Methods
void CpCommand::execute(FileSystem & fs) {
    string to;
    string from;

    size_t spacePos = getArgs().rfind(' ');
    to = getArgs().substr(spacePos);
    from = getArgs().substr(0, spacePos);

//    cout << to << endl;
//    cout << from << endl;

    vector<string> fromPath = readPath(from);
    vector<string> toPath = readPath(to);

    Directory *destination = isLegalPath(fs, toPath);

    string nameToCopy = fromPath[fromPath.size() - 1];

//    fromPath.pop_back();
//    Directory *source = isLegalPath(fs, fromPath);

    fromPath.pop_back();
    Directory* source;
    if(fromPath.size() == 0)
        source = &fs.getWorkingDirectory();
    else
        source = isLegalPath(fs,fromPath);

    if (destination == nullptr || source == nullptr) {      // path not exists
        cout << "No such file or directory" << endl;
        return;
    }


    File *fileToCopy = source->isFileExist(nameToCopy);
    Directory *dirToCopy = source->findChild(nameToCopy);

    if (fileToCopy == nullptr && dirToCopy == nullptr) {      // no such file or directory
        cout << "No such file or directory" << endl;
        return;
    }

    if (fileToCopy != nullptr) {

        if((destination->isFileExist(fileToCopy->getName()) != nullptr) || (destination->findChild(fileToCopy->getName()) != nullptr))
            return;
        destination->addFile(new File(*fileToCopy));
    }
    else {
        if ((destination->isFileExist(dirToCopy->getName()) != nullptr) ||
            (destination->findChild(dirToCopy->getName()) != nullptr))
            return;
        destination->addFile(new Directory(*dirToCopy));
    }
}



string CpCommand::toString()
{
    return "cp";
}



////                                                        MvCommand

//Constructor
MvCommand::MvCommand(string args): BaseCommand(args){}
MvCommand::~MvCommand(){}

//Methods
void MvCommand::execute(FileSystem & fs)
{
    string to;
    string from;

    size_t spacePos = getArgs().rfind(' ');
    to = getArgs().substr(spacePos);
    from = getArgs().substr(0, spacePos);

    vector<string> fromPath = readPath(from);
    vector<string> toPath = readPath(to);





    Directory *destination = isLegalPath(fs, toPath);




    string nameToMove = fromPath[fromPath.size() - 1];

    if(nameToMove == ".." && fromPath[0] != "/")        //TODO this is fixing the case of only ..
    {
        cout << "Can't move directory"<<endl;
        return;
    }



    fromPath.pop_back();
    Directory* source;
    if(fromPath.size() == 0)
        source = &fs.getWorkingDirectory();
    else
        source = isLegalPath(fs,fromPath);



    if (destination == nullptr || source == nullptr) {      // path not exists
        cout << "No such file or directory" << endl;
        return;
    }


    File *fileToMove = source->isFileExist(nameToMove);
    Directory *dirToMove = source->findChild(nameToMove);

    if (fileToMove == nullptr && dirToMove == nullptr) {      // no such file or directory
        cout << "No such file or directory" << endl;
        return;
    }

    if (fileToMove != nullptr)
    {
        destination->addFile(new File(fileToMove->getName(), fileToMove->getSize()));
        source->removeFile(fileToMove);
    }
    else
    {
        if(!isPossibleToDel(fs, dirToMove) )
        {
            cout << "Can't move directory"<<endl;
            return;
        }



        Directory *a = new Directory(*dirToMove);
        destination->addFile(a);
        a->setParent(destination);
        source->removeFile(dirToMove);
    }

    return;

}

string MvCommand::toString()
{
    return "mv";
}

////                                                        RenameCommand


//Constructor
RenameCommand::RenameCommand(string args): BaseCommand(args){}
RenameCommand::~RenameCommand(){}

//Methods
void RenameCommand::execute(FileSystem & fs)
{
    size_t posOfLastSpace = getArgs().rfind(' ');
    string name = getArgs().substr(posOfLastSpace+1);

    string readpath = getArgs().substr(0,getArgs().size() - name.size()-1);
    vector <string> path = readPath(readpath);

    string fileName = path[path.size()-1];


    path.pop_back();
    Directory* target;
    if(path.size() == 0)
        target = &fs.getWorkingDirectory();
    else
        target = isLegalPath(fs,path);



    if(target == nullptr)
    {
        cout << "The system cannot find the path specified" << endl;
        return;
    }

    File * fileToRename = target->isFileExist(fileName);

    if(target->isFileExist(name) != nullptr || target->findChild(name) != nullptr)
        return;

    if(fileToRename != nullptr) {

        fileToRename->setName(name);
        return;
    }
    else
    {
        Directory * dirToRename = target->findChild(fileName);
        if(dirToRename == nullptr) {
            cout << "No such file or directory" << endl;
            return;
        }
        if(&fs.getWorkingDirectory() == dirToRename){
            cout << "Can't rename the working directory" << endl;
            return;
        }
        if(!isPossibleToDel(fs,dirToRename )){      // TODO to check that adi was right about cant rename father name
            return;
        }

        dirToRename->setName(name);
        return;
    }


    return;

}



string RenameCommand::toString()
{
    return "rename";
}




////                                                        RmCommand


//Constructor
RmCommand::RmCommand(string args): BaseCommand(args){}
RmCommand::~RmCommand(){}

//Methods
void RmCommand::execute(FileSystem & fs)
{
    vector <string> path = readPath(getArgs());
    string nameToRemove = path[path.size()-1];

    if(nameToRemove == "~") {
        cout << "Can't remove directory" << endl;
        return;
    }

    path.pop_back();
    Directory* toRemoveFrom;
    if(path.size() == 0)
        toRemoveFrom = &fs.getWorkingDirectory();
    else
        toRemoveFrom = isLegalPath(fs,path);

    if(toRemoveFrom == nullptr)
    {
        cout << "No such file or directory" << endl;
        return;
    }



    File *fileToRemove = toRemoveFrom->isFileExist(nameToRemove);
    Directory *dirToRemove = toRemoveFrom->findChild(nameToRemove);

    if(fileToRemove == nullptr && dirToRemove == nullptr)
    {
        cout << "No such file or directory" << endl;
        return;
    }

    //removing a file
    if (fileToRemove != nullptr) {
        toRemoveFrom->removeFile(nameToRemove);
        return;
    }


    //removing a directory
    if(!isPossibleToDel(fs, dirToRemove))
    {
        cout << "Can't remove directory" << endl;
        return;
    }

    toRemoveFrom->removeFile(dirToRemove);

    return;
}


string RmCommand::toString()
{
    return "rm";
}






////                                                        HistoryCommand


//Constructor
HistoryCommand::HistoryCommand(string args, const vector<BaseCommand *> & history): BaseCommand(args) ,history(history) {}
HistoryCommand::~HistoryCommand(){}


//Methods
void HistoryCommand::execute(FileSystem & fs)
{
    int index = 0;

    for(size_t i = 0 ; i < history.size() ; i++)
    {
        cout << index << "\t" << history.at(i)->getArgs() <<  endl;
        index++;
    }

}

string HistoryCommand::toString()
{
    return "history";
}

////                                                        ErrorCommand


//Constructor
ErrorCommand::ErrorCommand(string args):BaseCommand(args){}
ErrorCommand::~ErrorCommand(){}

//Methods
void ErrorCommand::execute(FileSystem & fs)
{
    size_t firstSpace = getArgs().find(' ');
    string unknownCommand = getArgs().substr(0, firstSpace);

    cout << unknownCommand << ": Unknown command" << endl;
}

string ErrorCommand::toString()
{
    return "Unknown command";
}




////                                                        ExecCommand


//Constructor
ExecCommand::ExecCommand(string args, const vector<BaseCommand *> & history): BaseCommand(args) ,history(history) {}
ExecCommand::~ExecCommand(){}
//Methods
void ExecCommand::execute(FileSystem & fs)
{
    size_t posOfLastSpace = getArgs().rfind(' ');

    string commandIndx = getArgs().substr(posOfLastSpace+1);

    int indx = atoi (commandIndx.c_str());

    int historySize = history.size();

    if(indx < 0 || indx >= historySize) {
        cout << "Command not found" << endl;
        return;
    }


    history[indx]->execute(fs);

}

string ExecCommand::toString()
{
    return "exec";
}

////                                                        VerboseCommand

//Constructor
VerboseCommand::VerboseCommand(string args):BaseCommand(args){}
VerboseCommand::~VerboseCommand(){}

//Methods
void VerboseCommand::execute(FileSystem & fs)
{
    size_t posOfLastSpace = getArgs().rfind(' ');

    string input = getArgs().substr(posOfLastSpace+1);

    int ver = atoi (input.c_str());

    if((input.size() > 1 )||( (verbose !=0 )& (verbose !=1 )& (verbose !=2) & (verbose !=3)))
    {
        cout << "Wrong verbose input"<< endl;
    }

    if(ver==0)
        verbose = 0;
    if(ver==1)
        verbose = 1;
    if(ver==2)
        verbose = 2;
    if(ver==3)
        verbose = 3;

}

string VerboseCommand::toString()
{
    return "verbose";
}