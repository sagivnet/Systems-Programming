
#include "../include/Environment.h"
#include "../include/GlobalVariables.h"


//extern unsigned int verbose = 0;


//Constructor
Environment::Environment():commandsHistory(), fs(){}

//Destructor
Environment::~Environment()
{
    for(size_t i=0 ; i< commandsHistory.size() ; i++)
        delete(commandsHistory[i]);

    if(verbose == 1 || verbose == 3)
        cout<< "Environment::~Environment()" << endl;
}

//Opertaor =
Environment& Environment::operator=(Environment& other)
{

    fs=other.fs;

    commandsHistory = other.commandsHistory;



    if(verbose == 1 || verbose == 3)
        cout<< "Environment& Environment::operator=(Environment& other)" << endl;

    return *this;
}

//Copy constructor
Environment::Environment(FileSystem & other):commandsHistory() , fs()
{
    if(verbose == 1 || verbose == 3)
        cout<< "Environment::Environment(FileSystem & other)" << endl;
}

//Move constructor
Environment::Environment (FileSystem && other):commandsHistory() , fs()
{

    if(verbose == 1 || verbose == 3)
        cout<< "Environment::Environment (FileSystem && other)" << endl;
}

//Move assignment
Environment& Environment::operator=(Environment && other)
{

    if(verbose == 1 || verbose == 3)
        cout<< "Environment& Environment::operator=(Environment && other)" << endl;

    return *this;
}

//Methods
void Environment::start()
{
    verbose = 0;
    BaseCommand* command;
    string input;


    cout << fs.getWorkingDirectory().getAbsolutePath() << ">";
    getline (cin, input);

    while (input != "exit")
    {



        if (input.find("pwd")==0)
        {
            command = new PwdCommand(input);

            if(( (verbose == 2) || (verbose ==3)))
                cout << input << endl;

            command->execute(fs);
            addToHistory(command);
        }
        else if (input.find("cd")==0)
        {
            command = new CdCommand(input);

            if(( (verbose == 2) || (verbose ==3)))
                cout  << input << endl;

            command->execute(fs);
            addToHistory(command);
        }
        else if (input.find("ls")==0)
        {
            command = new LsCommand(input);

            if(( (verbose == 2) || (verbose ==3)))
                cout << input << endl;

            command->execute(fs);
            addToHistory(command);
        }
        else if (input.find("mkdir")==0)
        {
            command = new MkdirCommand(input);

            if(( (verbose == 2) || (verbose ==3)))
                cout  << input << endl;

            command->execute(fs);
            addToHistory(command);
        }
        else if (input.find("mkfile")==0)
        {
            command = new MkfileCommand(input);

            if(( (verbose == 2) || (verbose ==3)))
                cout << input << endl;

            command->execute(fs);
            addToHistory(command);
        }
        else if (input.find("cp")==0)
        {
            command = new CpCommand(input);

            if(( (verbose == 2) || (verbose ==3)))
                cout  << input << endl;

            command->execute(fs);
            addToHistory(command);
        }
        else if (input.find("mv")==0)
        {
            command = new MvCommand(input);

            if(( (verbose == 2) || (verbose ==3)))
                cout << input << endl;

            command->execute(fs);
            addToHistory(command);
        }
        else if (input.find("rename")==0)
        {
            command = new RenameCommand(input);

            if(( (verbose == 2) || (verbose ==3)))
                cout  << input << endl;

            command->execute(fs);
            addToHistory(command);
        }
        else if (input.find("rm")==0)
        {
            command = new RmCommand(input);

            if(( (verbose == 2) || (verbose ==3)))
                cout  << input << endl;

            command->execute(fs);
            addToHistory(command);
        }
        else if (input.find("history")==0)
        {
            command = new HistoryCommand(input, getHistory());

            if(( (verbose == 2) || (verbose ==3)))
                cout << input << endl;

            command->execute(fs);
            addToHistory(command);
        }
        else if (input.find("exec")==0)
        {
            command = new ExecCommand(input, getHistory());

            if(( (verbose == 2) || (verbose ==3)))
                cout << input << endl;

            command->execute(fs);
            addToHistory(command);
        }
        else if (input.find("verbose")==0)
        {
            command = new VerboseCommand(input);
            command->execute(fs);
            addToHistory(command);
        }
        else
        {
            command = new ErrorCommand(input);

            if(( (verbose == 2) || (verbose ==3)))
                cout << input << endl;

            command->execute(fs);
            addToHistory(command);
        }

//        if((input.find("verbose")==0 )&( (verbose == 2) || (verbose ==3)))
//            cout <<fs.getWorkingDirectory().getAbsolutePath() << ">"<< input << endl;


        cout << fs.getWorkingDirectory().getAbsolutePath() << ">";
        getline (cin, input);
    }
}




FileSystem& Environment::getFileSystem()  // Get a reference to the file system
{
    FileSystem & copy = fs;
    return copy;

}


void Environment::addToHistory(BaseCommand *command) // Add a new command to the history
{
    commandsHistory.push_back(command);
}




const vector<BaseCommand*>& Environment::getHistory() const  // Return a reference to the history of commands
{
    return commandsHistory;
}
