# toya

This repository contains the implementation of my bachelor's thesis: the copmiler for toya.

## How to run

1. Write your program in a file ending with a .toya extension.
2. Run the program and supply the path to the source code files relative to the source directory. In the case of the example toya program it would be `src\main\resources\Main.toya`. This step will produce a class file in your root directory
3. Execute the compiled toya program with `java Main`

## Example programs

**Hello World**
```
function main(args: string[]) {
    print("Hello World")
}
```

**Functions**
```
function title() {
    print("This is an addition:")
}

function add(lhs: int, rhs: int) -> int {
    lhs + rhs
}

function main(args: string[]) {
    title()
    print(add(1,2))
}
```

**Variables**
```
function someFunction() -> int {
    return 8
}

function main(args: string[]) {
    var number = 123
    var word = "Hello World"
    var double = 123.456
    var bool = true
    var result = someFunction()

    print(number)
    print(word)
    print(double)
    print(bool)
    print(result)
}
```

**Arrays**
```
function main(args: string[]) {
    var arr = new int[8]
    arr[3] = 15

    print(arr[3])
    print(arr[4])
}
```

**If expressions**
```
function main(args: string[]) {
    var value = if (3 > 4) 5 else 6
    print(value)

    if(3 < 4) {
        print("true branch")
    } else {
        print("false branch")
    }
}
```

**For loops**
```
function main(args: string[]) {
    var n = 200

    for (var i = 0; i <= n; i = i+10) {
        print(i)
    }
}
```

**Complex program**
```
function printIntro(n: int) {
    print("Welcome to a simple toya program")
    print("Calculating numbers up to: ")
    print(n)
    print("---------")
}

function main(args: string[]) {
    var n = 200
    printIntro(n)

    for (var i = 0; i <= n; i = i+10) {
        if (i == 20) {
            print("i is 20:")
        }
        print(i)
    }

    var boolArr = new boolean[2]
    boolArr[1] = true
    print(boolArr[0])
    print(boolArr[1])
}
```
