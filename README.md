# Notes on the Module "Examples"

## What Does It Do?

Guess what: This module contains example pograms (no, really?!).

## What the Programs are
Theses programs are simple *example* programs demonstrating the correct / suggested use of the 
KMyMoney 
API library, partially replacing a user documentation that does not exist in the current version.

## What the Programs are Not
* They are *not* general-purpose tools. 

  Yes, you will partially find "siblings" of these programs in the module "Tools", and these tools have 
  partially evolved from these examples.

  However, you will also see that it takes a lot more to make a general-purpose tool of such a 
  program -- not only technical work like parsing the input data from the command line and 
  exception handling, but also what in the business world is called specification work: 
  What exactly is needed, what are the *use cases* (for the older guys like the author of 
  this document) or the *user stories* (for the younger ones) resp., plausibility tests, etc.

* They are *not* test cases.

  Granted, the programs partially refer to the test data 
  KMyMoney 
  file (which normally should not be the case, but things are not cleanly separated yet in this 
  stage of development), and granted, one could -- within reason -- see parts of the programs 
  as some sort of "high-level" test cases.

  However, in order to really be test cases, they would have to be embedded in a test environment, 
  expected outcomes would have to be formally specified (and checked!) and some more things. 

  Test cases are where they belong to: in the 
  KMyMoney 
  API (Extensions) library module, under the directory `test`.

## How To Use Them
Each of these programs contains a section for example data. It has to be adapted to your needs, 
especially to the 
KMyMoney
file that you are going to use it with.

[ Part of the test data (e.g., the account IDs) refers to the data in the test data 
KMyMoney 
file, but that does not mean that they actually have to / should be used with this file. 
Instead, the references are only used to clarify the meaning of the parameter / variable, 
if appropriate. ]

Each of the example programs compiles as it is (the author always gets mad when he sees 
example code in other projects that does not even compile, let alone work). And they are 
not just code snippets, but complete, self-contained programs that you can actually run.

Thus, the suggested workflow is:

1. Copy one of them into your own project.
2. Adapt the data to the KMyMoney file that you are using.
3. Compile.
4. Do a test run and see whether it actually does what you expect.
5. Adapt the code to your needs / integrate it into your project.

## More Example Files
https://www.gnucash.org/docs/examples/

## What is This Repo's Relationship with the Other Repos?

* This is a module-level repository which is part of a multi-module project, i.e. it has a parent and several siblings. 

  [Parent](https://github.com/jross765/JKMyMoneyLibNTools.git)

* Under normal circumstances, you cannot compile it on its own (at least not without further preparation), but instead, you should clone it together with the other repos and use the parent repo's build-script.

* This repository contains no history before V. 1.7 (cf. notes in parent repo).

