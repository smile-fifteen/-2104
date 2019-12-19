/*
 * Templated class.
 */
template <class T>
class MyTemplate {
public:
    T myTMethod() {return T();}
};

/*
 * double template specialisation.
 */
template<>
class MyTemplate<double> {
public:
    double myDoubleMethod() {return 1.0;}
};

/*
 * char template specialisation.
 */
template<>
class MyTemplate<char> {
public:
    char myCharMethod() {return 'H';}
};

/*
 * Templated pointer type.
 */
template <class T>
class MyPtr {
public:
    MyTemplate<T> *operator->() {return &p;}
    MyTemplate<T> &operator*() {return p;}
private:
    MyTemplate<T> p;    // Contained object.
};

struct iz147518_Z {
    int i;
};

/*
 * Main.
 */
int iz147518_main(int argc, char** argv) {
    MyPtr<int> pi;              // Templated pointer to int templated type.
    pi->myTMethod();            // Call method on contained object.
    (*pi).myTMethod();          // Call method on contained object.

    MyPtr<char> pc;             // Templated pointer to char specialised type.
    pc->myCharMethod();         // FIXME: Call method on contained object.
    (*pc).myCharMethod();       // FIXME: Call method on contained object.

    MyPtr<double> pd;           // Templated pointer to double specialised type.
    pd->myDoubleMethod();       // FIXME: Call method on contained object.
    (*pd).myDoubleMethod();     // FIXME: Call method on contained object.

    MyPtr<iz147518_Z> pz;
    pz->myTMethod().i;       // FIXME: Call method on contained object.

    return 0;
}

