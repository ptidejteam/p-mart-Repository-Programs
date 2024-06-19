print(":::: Foo ::::");
defineClass("Foo")
var foo = new Foo();
print(foo);
print(foo.counter);
print(foo.counter);
foo.resetCounter();
print(foo.counter);
print(foo.varargs(3, "hi"));


print(":::: File ::::");
defineClass("File")
var file = new File("myfile.txt");
file.writeLine("one");
file.writeLine("two");
file.writeLine("thr", "ee");
file.close();
var a = file.readLines();
print(a);

print(":::: Matrix ::::");
defineClass("Matrix")
m = new Matrix(2); 
print(m);
m[0][0] = 3;
print(m[0]);
print(m[1]);
print(m.dim); 
m.dim = 3;
print(m.dim); 
