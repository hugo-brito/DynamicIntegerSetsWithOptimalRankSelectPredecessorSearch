Dynamic Integer Sets with Optimal Rank Select, and Predecessor Search
============
This repository features the work developed in the context of the thesis around the paper [Dynamic Integer Sets with Optimal Rank, Select, and Predecessor Search](https://arxiv.org/pdf/1408.3045) [Patrascu and Thorup, FOCS 2014]

## Project Agreement
### Problem Domain
Since their inception, roughly 30 years ago, succinct data structures have helped encoding large chunks of data while allowing operations on that data efficiently. The pioneer of this idea, Guy Jacobson, states that while one could compress data such that it takes less space, this is less than optimal since once compressed, the data cannot be queried efficiently. On the other hand, succinct data structures are more suited since they combine size reduction with data accessibility [Jacobson, 1988]. They accomplish so by encoding data efficiently. This means that everything is accessed in-place, by reading bits at various positions in the data.

This idea has been proven useful: the world is full of data, which grows by the day, and the convenience of querying such data is vitally important. Practical applications range from areas such as database management systems (DBMS), text indexing, bioinformatics, big data and data mining applications and beyond.

### Domain overview
Despite the numerous applications and the obvious advantages, for the particular problem _Rank_, _Select_, _Update_, according to a preliminary literature search, there has not been found a public record of an implementation that achieves optimality regarding the running times of the stated operations. This means that, once the original piece of data which the data structure maps to is altered, it becomes void since it might not necessarily represent its non-encoded counterpart accurately. In order to be usable once more, it has to be re-encoded to reflect the changes, which in practice translates to a trade-off: the gains attained by building and having available a data structure that allows fast queries become less optimal when it has to be re-computed several times due to changes in the original data counterpart.

### Problem Formulation
This original idea proposed by Guy Jacobson has branched in different directions:
- A more recent publication reports on a data structure to perform _Rank_ and _Select_ operations over a bit array, which effectively consists of a **static** succinct data structure using $O(n/log_2(n))$ extra space [González et al., 2005].
- It is possible to represent a binary search tree by using a bit vector of $2n+1$ size [Munro, 2004]. This is a **non-succinct** approach to the problem, but once the tree is represented by such bit vector, querying:
    - Left child ($x$) = $2$ rank($x$).
    - Right child ($x$) = $2$ rank($x$)$+1$.
    - Parent ($x$) = select($\lfloor x/2 \rfloor$)
- A recent publication claims that an implementation of a **Dynamic** _Rank_, _Select_ and _Predecessor Search_ data structure with **Optimal** asymptotic running times for these queries is possible [Patrascu and Thorup, 2014].

This last publication also establishes that: 
- insert($x$) sets $S=S \cup \{x\}$.
- delete($x$) sets $S=S \setminus \{x\}$.
- predecessor($x$) returns $max\{y\in S\ |\ y < x\}$.
- successor($x$) returns $min\{y\in S\ |\ y \geq x\}$.
- rank($x$) returns $\#\{y\in S\ |\ y < x\}$.
- select($i$) returns $y \in S$ with rank($y$) = $i$, if any.

This thesis is aimed at finding to which extend the claim from Patrascu and Thorup [FOCS 2014] is supported by practical evidence.

### Method
The paper _[Dynamic Integer Sets with Optimal Rank, Select, and Predecessor Search](https://arxiv.org/pdf/1408.3045)_ by Patrascu and Thorup [FOCS 2014] will be reviewed and its content will be used as a starting point for the following:
- Exposition of the data structure present in the aforementioned paper. Since the paper in question is expected to be vague at times, the goal is to make the data structure (and its algorithmic operations) easier to understand by filling in the gaps left by the paper.
- Implementation of the data structure as close as possible to the description including its standard operations (_Insert_, _Delete_, _Predecessor_, _Rank_ and _Select_).
- Elaboration of a small survey on existing implementations that support some of the standard operations mentioned in the previous bullet point.
- Implementation of a non-succinct version of the data structure, with polylog n query and update times, as a reference point for the experiments.
- Design and execution of a suitable experiment/benchmark to assess if, when compared with the implementations surveyed in the previous point, it exists any gain in performance when using the data structure in question.
- Evaluation and reflection of all the previous points.

### Disclaimer
This proposal is to be perceived as a plan, not as a promise. Since a large part of the project will consist of researching and experimenting with new concepts, the need to diverge from what is stated here might arise.

### Deliverables
- Written report.
- Product in form of code including implementation of the _Dynamic Integer Sets with Optimal Rank, Select, and Predecessor Search_ data structure, benchmark framework and other support custom programs.

### References
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


[Succinct static data structures](https://dl.acm.org/doi/book/10.5555/915547)
```
@article{jacobson1988succinct,
  title={Succinct static data structures},
  author={Jacobson, Guy Joseph},
  year={1988},
  publisher={Carnegie Mellon University}
}
```


[Succinct data structures](https://pdfs.semanticscholar.org/b1ab/54f86325760d74d88aa1619b7fa4712637d6.pdf)
```
@article{munro2004succinct,
  title={Succinct data structures},
  author={Munro, J Ian},
  journal={Electr. Notes Theor. Comput. Sci.},
  volume={91},
  pages={3},
  year={2004}
}
```


[Practical implementation of rank and select queries](http://personales.dcc.uchile.cl/~gnavarro/ps/wea05.pdf)
```
@inproceedings{gonzalez2005practical,
  title={Practical implementation of rank and select queries},
  author={Gonz{\'a}lez, Rodrigo and Grabowski, Szymon and M{\"a}kinen, Veli and Navarro, Gonzalo},
  booktitle={Poster Proc. Volume of 4th Workshop on Efficient and Experimental Algorithms (WEA)},
  pages={27--38},
  year={2005}
}
```