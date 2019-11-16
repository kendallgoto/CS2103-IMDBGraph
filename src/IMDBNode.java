abstract class IMDBNode implements Node {
    final private String _name;
    public IMDBNode(String name) {
        _name = name;
    }
    public String getName() {
        return _name;
    }
}
