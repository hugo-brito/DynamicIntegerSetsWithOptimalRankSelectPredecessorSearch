Dynamic Integer Sets with Optimal Rank Select, and Predecessor Search
============
This repository features the work developed in the context of the thesis around the paper [Dynamic Integer Sets with Optimal Rank, Select, and Predecessor Search](https://arxiv.org/pdf/1408.3045) [Patrascu and Thorup, FOCS 2014]

## Contents
The repository contains:
- Writen report: ``somefile.pdf``, located in the root folder.
- Source code of the report: ``.\src\tex`` folder.
- Documentation of the feature code, in the Javadoc format: ``.\src\javadoc`` folder.
- Source code of the implemented data structures and algorithms: ``.\src\main\java\integersets`` folder.
- Source code of testing classes: ``.\src\test\java`` folder.

## Abstract
This thesis investigates the theoretical data structure presented in the «Dynamic Integer Sets with Optimal Rank, Select, and Predecessor Search» paper, authored by Pătrașcu and Thorup.
The authors claim that their proposal solves the dynamic predecessor problem optimally [Pătrașcu and Thorup, 2014].
Concretely, we follow the paper closely up to chapter 3.1 (inclusive), implementing the data structure and its algorithms, discussing and evaluating the running times.
The data structure they propose denoted _Dynamic Fusion Node_, is made possible due to the smart use of techniques such as bitwise tricks, word-level parallelism, key compression, and wildcards (denoted "don't cares" in this report and also by Pătrașcu and Thorup), which for sets of size _n = w<sup>O(1)</sup>_ and in the word-RAM model take _O(1)_ time.
By using the _Dynamic Fusion Node_ in the implementation of a _k_-ary tree, thus enabling the sets to be of arbitrary size, the running times are _O(_ log<sub>_w_</sub> _n)_, proven by Fredman and Saks to be optimal [Fredman and Saks, 1989].

Using the [Pătrașcu and Thorup, 2014] paper as our primary reference, and after framing the topic within its context, we explore the essential tools and algorithms used by Pătrașcu and Thorup in their proposal.
We will build, explore, and implement a library of relevant functions and algorithms used by the _Dynamic Fusion Node_.
Based on the theoretical algorithms presented in [Pătrașcu and Thorup, 2014], the implementation is presented in iterative steps, starting from a naive way up to the insertion method while using all the algorithms and techniques described up to that point.
We validate the implemented algorithms and data structures with correctness tests.
Finally, we conclude the project, leaving some remarks and suggestions for future work, which include the necessary steps to make implementation _O(1)_, which at the moment is still bound by some subroutines that take more than constant time.

Up to the point where this project ends, Pătrașcu and Thorup's proposal seems sound, as we have been able to implement their data structure proposal using mostly _O(1)_ operations of the word RAM model.

The Java programming language is used throughout the project, and the resulting code is publicly available on the GitHub repository sited at [https://github.com/hugo-brito/DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch](https://github.com/hugo-brito/DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch) and subject to an MIT License.

## Keywords
Algorithms, Dynamic Predecessor Problem, Integer Sets, Key Compression, Bitwise Operations, Most Significant Set Bit, Word-level Parallelism, Dynamic Fusion Node.

## Main Reference
[Dynamic Integer Sets with Optimal Rank, Select, and Predecessor Search](https://arxiv.org/pdf/1408.3045)
```
@inproceedings{patrascu2014dynamic,
  title={Dynamic integer sets with optimal rank, select, and predecessor search},
  author={Patrascu, Mihai and Thorup, Mikkel},
  booktitle={2014 IEEE 55th Annual Symposium on Foundations of Computer Science (FOCS)},
  pages={166--175},
  year={2014},
  organization={IEEE}
}
```