namespace bug238847_1 {

    template <typename T>  
    struct my_allocator_238847_1 {

        typedef T value_type;

        typedef T* pointer;

        int foo();

        template <typename T1>
        struct my_rebind {
            typedef my_allocator_238847_1<T1> other;
        };

    };
    
    template<typename _Alloc, typename _Tp>
    struct my_alloc_rebind_238847_1
    {
      typedef typename _Alloc::template my_rebind<_Tp>::other __type;
    };    

    template <typename _Alloc>
    struct my_allocator_traits_238847_1 {

        typedef typename _Alloc::value_type value_type;

        template <typename T>        
        static typename T::pointer pointer_helper(T*);

        typedef decltype(pointer_helper<_Alloc>((_Alloc*)0)) __pointer;

        typedef __pointer pointer;


        template<typename _Tp>
        using rebind_alloc = typename my_alloc_rebind_238847_1<_Alloc, _Tp>::__type;
    };

    template<typename _Alloc>
     struct my_alloc_traits_238847_1 {
        typedef _Alloc allocator_type;
        typedef my_allocator_traits_238847_1<_Alloc>           _Base_type;

        typedef typename _Base_type::value_type         value_type;

        typedef typename _Base_type::pointer            pointer;

        template<typename _Tp>
        struct rebind_helper
        { typedef typename _Base_type::template rebind_alloc<_Tp> other; };    
    };    
    
    template <typename T, typename Allocator>
    struct vector_base_238847_1 {

        typedef typename my_alloc_traits_238847_1<Allocator>::template rebind_helper<T>::other _Tp_alloc_type;

        typedef typename my_alloc_traits_238847_1<_Tp_alloc_type>::pointer pointer;    

    };    

    struct MyClass_238847_1 {
        void boo() {}
    };

    int main()
    {
        vector_base_238847_1<MyClass_238847_1, my_allocator_238847_1<MyClass_238847_1> >::_Tp_alloc_type::pointer ptr1;
        ptr1->boo();        
        
        vector_base_238847_1<MyClass_238847_1, my_allocator_238847_1<MyClass_238847_1> >::pointer ptr2;
        ptr2->boo();
        
        return 0; 
    }  
}