package DynamicIntegerSetsWithOptimalRankSelectPredecessorSearch;

public class FusionTree implements RankSelectPredecessorUpdate {

  static class FTreeNode<E> extends Node<E> {

    FTreeNode<E> left;
    FTreeNode<E> right;

    public FTreeNode(final E key) {
      super(key);
    }

    @Override
    Node<E> left() {
      return left;
    }

    @Override
    Node<E> right() {
      return right;
    }
  }
  

  @Override
  public void insert(long x) {
    // TODO Auto-generated method stub

  }

  @Override
  public void delete(long x) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean member(long x) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public long predecessor(long x) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long successor(long x) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long rank(long x) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long select(long i) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long size() {
    // TODO Auto-generated method stub
    return 0;
  }
  
}