typedef union bug200140_a {
    int x;
} bug200140_aa;
typedef struct bug200140_b {
    struct bug200140_b *y;
    union bug200140_u *z;
} bug200140_bb;
typedef struct bug200140_c {
    struct bug200140_b *q, *w;

} bug200140_cc;
typedef struct bug200140_d {
    union node_u *t,*u;
} bug200140_dd;
typedef union e {
    struct bug200140_d p;
    struct bug200140_c o;
} bug200140_ee;

typedef struct bug200140_g
{
    union
    {
        bug200140_ee *n;
    }
    m;
}
bug200140_gg;

int bug200140_k;

bug200140_gg **bug200140_h;

bug200140_gg ** bug200140_foo() {
    return 0;
}

#define bug200140_AAA(i,j) ((j)<bug200140_k ? bug200140_h : bug200140_foo(j))[(i)*bug200140_k + (j)]


int main(int argc, char** argv) {
    int i,j;
    (bug200140_AAA (i, j)->m.n);
    ((j)<bug200140_k ? bug200140_h : bug200140_foo(j))[(i)*bug200140_k + (j)]->m.n;
    return 0;
}