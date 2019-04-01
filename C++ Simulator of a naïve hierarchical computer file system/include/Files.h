#ifndef FILES_H_
#define FILES_H_

#include <string>
#include <vector>
#include <iostream>

using namespace std;






class BaseFile {

private:
    string name;

public:
    BaseFile(string name);
    string getName() const;
    void setName(string newName);
    virtual int getSize() = 0;

    //Our Methods
    virtual ~BaseFile();
    virtual bool isFile()=0;

};








class File : public BaseFile
{
private:
    int size;

public:
    File(string name, int size); // Constructor
    int getSize(); // Return the size of the file

    ~File();
    bool isFile();


};







class Directory : public BaseFile {

private:
    vector<BaseFile*> children;
    Directory *parent;

public:
    Directory(string name, Directory *parent); // Constructor
    Directory *getParent() const; // Return a pointer to the parent of this directory
    void setParent(Directory *newParent); // Change the parent of this directory
    void addFile(BaseFile* file); // Add the file to children
    void removeFile(string name); // Remove the file with the specified name from children
    void removeFile(BaseFile* file); // Remove the file from children
    void sortByName(); // Sort children by name alphabetically (not recursively)
    void sortBySize(); // Sort children by size (not recursively)
    vector<BaseFile*> getChildren(); // Return children
    int getSize(); // Return the size of the directory (recursively)
    string getAbsolutePath();  //Return the path from the root to this

    //Our methods


    bool isFile();
    void clearChildren();
    vector<BaseFile*> cloneChildren();
    Directory* findChild(string name);
    File *  isFileExist(string name);

    // Rule of five
    Directory& operator=(Directory& other);
    Directory( Directory& other);
    ~Directory();
    Directory (Directory && other);
    Directory& operator=(Directory && other);



};

#endif