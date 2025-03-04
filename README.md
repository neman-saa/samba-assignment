Assignment
Define a data type D that represents a document subdivided horizontally or vertically into 1 or
more cells that in turn can be further subdivided or can hold a value of some type A (A is a
parameter). Equip the new data type D with

f[M[_]: Monad, A, B]: (A => M[B]) => D[A] => M[D[B]],

such that

f[Id](identity) = identity,
f [Option](Some(_)) = Some(_),

where Id[A] = A.