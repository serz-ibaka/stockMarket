#ifndef _EXCEPTIONS_H_
#define _EXCEPTIONS_H_

#include <exception>

class ErrorUsernameExists : public std::exception {

};

class ErrorUsernameNotExists : public std::exception {

};

class ErrorWrongPassword : public std::exception {

};

#endif