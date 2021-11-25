#include"../include/Student.h"
#include<iostream>
Student::Student(string n,int a){
    this->name = n;
    this->age = a;
}
void Student::print(){
    cout<<"name:"<<name<<" age:"<<age<<endl;
}