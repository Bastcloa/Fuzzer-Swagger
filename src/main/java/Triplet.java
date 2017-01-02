class Triplet<T, V, U> {
    private final T m_first;
    private final V m_second;
    private final U m_third;

    public Triplet(T first, V second, U third) {
        m_first = first;
        m_second = second;
        m_third = third;
    }

    public T first() {
        return m_first;
    }

    public V second() {
        return m_second;
    }
    
    public U third() {
        return m_third;
    }
}