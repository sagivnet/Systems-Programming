

#include "../include/Files.h"
#include "../include/GlobalVariables.h"
#include <algorithm>
#include <s9core.h>
//#include <s9core.h>

//                                                          Base File

//Constructor
BaseFile::BaseFile(string name) :name(name) {}

//Destructor
BaseFile::~BaseFile(){}


//Methoods

string BaseFile::getName() const
{
    return name;
}

void BaseFile::setName(string newName)
{
    name=newName;
}

int BaseFile::getSize() //Implementions by sub class
{
    return 0;
}





//                                                                  File

//Constructor
File::File(string name, int size) : BaseFile(name), size(size){}


//Destructor
File::~File(){}


//Methods
int File::getSize()
{
    return size;
}

bool File::isFile()
{
    return true;
}





//                                                               Directory




//Constructor
Directory::Directory(string name, Directory *parent): BaseFile(name),children(), parent()
{
    if(parent != nullptr)
        this->parent = parent;
}

//Copy Constructor
Directory::Directory(Directory& other):Directory(other.getName(),other.getParent())
{
    children = other.cloneChildren();
    if(verbose == 1 || verbose == 3)
        cout<< "Directory::Directory(Directory& other)" << endl;

}

//Destructor
Directory::~Directory()
{
    clearChildren();
    if(verbose == 1 || verbose == 3)
        cout<< "Directory::~Directory()" << endl;
}

//Operator =
Directory& Directory:: operator=(Directory& other)  //copy the content of other folder but NOT parent
{
    clearChildren();
    children = other.cloneChildren();;
    setName(other.getName());

    if(verbose == 1 || verbose == 3)
        cout<< "Directory& Directory:: operator=(Directory& other)" << endl;

    return *this;
}
//Move constructor
Directory::Directory (Directory && other): BaseFile(other.getName()),children(other.children) , parent(other.parent)
{



        other.parent = nullptr;
        other.children.clear();
        other.setName("");

        if (verbose == 1 || verbose == 3)
            cout << "Directory::Directory (Directory && other)" << endl;

}

//Move assignment
Directory& Directory ::operator=(Directory && other)
{
    if (this != &other)
    {
        parent = other.parent;
        setName(other.getName());
        children = other.children;

        other.setName("");
        other.children.clear();
        other.parent = nullptr;
    }

    if(verbose == 1 || verbose == 3)
        cout<< "Directory& Directory ::operator=(Directory && other)" << endl;

    return *this;
}

//Methods
Directory* Directory :: getParent() const
{
    return parent;
}

void Directory :: setParent(Directory *newParent)
{
    if(parent!=nullptr)
    {
        int size = parent->children.size();
        for(int i = 0 ; i<size ; i++)
            if(parent->children[i] == this)
                parent->children.erase(children.begin()+i);
    }

    parent = newParent;

//    if(parent!= nullptr)
//    {
//        parent->children.push_back(this);
//    }

}

void Directory:: addFile(BaseFile* file)
{
    children.push_back(file);

}

void Directory::removeFile(string name)
{
    string checkName;
    vector<BaseFile*>::iterator it;
    bool found = false;

    int size = children.size();
    for (int i = 0 ; i<size && !found ; i++)
    {
        checkName = children[i]->getName();


        if (name == checkName)
        {

            delete(children[i]);
            children.erase(children.begin() + i);
            found=true;
        }
    }



    return;





}

void Directory::removeFile(BaseFile* file)
{

    vector<BaseFile*>::iterator it;
    bool found = false;


    for (size_t i = 0 ; i<children.size() && !found ; i++)
    {
        if (children[i] == file)
        {

            delete(children[i]);
            children.erase(children.begin() + i);
            found=true;
        }
    }
    return;





}




void Directory::sortByName()
{
    sort(children.begin(),children.end(),[](BaseFile*& a, BaseFile*& b)->   bool { return (a->getName() < b->getName()); });
}




void Directory:: sortBySize()
{
    sort(children.begin(),children.end(),[](BaseFile*& a, BaseFile*& b)->   bool { return (a->getSize() < b->getSize()); });
}


vector<BaseFile*> Directory:: getChildren()
{
    return children;
}

int Directory:: getSize()    // Return the size of the directory (recursively)
{
    int sum=0;
    vector<BaseFile*>::iterator it;
    for (it = children.begin(); it != children.end(); it++)
    {
        sum += (*it)->getSize();
    }
    return sum;
}
string Directory:: getAbsolutePath()          //Return the path from the root to this
{
    if(this->getParent()== nullptr)
        return "/";
    vector<string> path;
    string absolutePath ;
    Directory *tmp = this ;
    while(tmp->getParent()!= nullptr)
    {
        path.push_back(tmp->getName());
        tmp=tmp->getParent();
    }
    vector<string>::iterator it;
//    cout << "/";
    for (it=path.begin(); it != path.end(); it++)
    {
        absolutePath = "/"+*it+ absolutePath;
    }
    return absolutePath;


}


bool Directory::isFile()
{
    return false;
}


vector<BaseFile*> Directory::cloneChildren()
{
    vector<BaseFile*> copy;         //TODO Is it ok???

    int size = children.size();
    for(int i=0 ; i<size ; i++)
    {
        if(children[i]->isFile())
          //  copy.push_back(new File(children[i]->getName(),children[i]->getSize()));
            copy.push_back(new File(children[i]->getName(),children[i]->getSize()));
        else {
            Directory* a =  (Directory*)(children[i]);
            copy.push_back(new Directory(*a));
        }
    }
    return copy;
}



void Directory::clearChildren()
{
    int size = children.size();
    for(int i=0 ; i<size ; i++)
    {
        if(children[i]->isFile())
            delete dynamic_cast<File *>(children[i]);
        else
            delete dynamic_cast<Directory *>(children[i]);
    }
    children.clear();
}

Directory* Directory::findChild(string name)
{

    string checkName;
    vector<BaseFile*>::iterator it;
    bool found = false;

    for (it = children.begin(); it != children.end()&& !found; it++)
    {
        checkName = (*it)->getName();
        if (name == checkName)
        {
            if(!(*it)->isFile()) {
                found = true;
                return (Directory*)(*it);
            }

        }
    }
return nullptr;
}

File* Directory:: isFileExist(string name)
{
    int size = children.size();

    for(int i=0; i<size; i++)
    {
        if (children[i]->getName() == name  && children[i]->isFile())
            return (File*)children[i];
    }
    return nullptr;
}



