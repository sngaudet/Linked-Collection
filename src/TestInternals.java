import edu.uwm.cs351.LinkedCollection;
import junit.framework.TestCase;

@SuppressWarnings("unchecked")
public class TestInternals extends TestCase {

	protected LinkedCollection<String> self;
	protected LinkedCollection.Spy spy = new LinkedCollection.Spy();

	String[] e = new String[] {
			null, new String("A"), new String("B"), new String("C"), 
			new String("D"), new String("E") };
	String[] f = new String[] {
			null, new String("A"), new String("B"), new String("C"), 
			new String("D"), new String("E") };
	LinkedCollection.Spy.Node<String>[] m, n, p;
	LinkedCollection.Spy.Node<String>[] s;
	
	@Override
	protected void setUp() {
		m = new LinkedCollection.Spy.Node[e.length];
		n = new LinkedCollection.Spy.Node[f.length];
		p = new LinkedCollection.Spy.Node[e.length];
		s = new LinkedCollection.Spy.Node[3];
		
		for (int i=0; i < n.length; ++i) {
			n[i] = spy.makeNode(f[i]);
		}
		for (int i=0; i < m.length; ++i) {
			m[i] = spy.makeNode(e[i]);
			p[i] = spy.makeNode(e[i]);
		}
		for (int i=0; i < s.length; ++i) {
			s[i] = spy.makeSelfRef();
		}
	}

	
	/// testAx: testing addNodeAfter
	
	public void testA1() {
		spy.linkForward(s[0], s[0]);
		spy.linkBackward(s[0], s[0]);
		self = spy.createCol(s[0], 3, 15);
		spy.testAddNodeAfter(self, s[0], m[4]);
		assertSame(m[4], s[0].getPrev());
		assertSame(m[4], s[0].getNext());
		assertSame(s[0], m[4].getPrev());
		assertSame(s[0], m[4].getNext());		
	}
	
	public void testA2() {
		spy.linkForward(s[0], p[4], s[0]);
		spy.linkBackward(s[0], p[4], s[0]);
		spy.linkForward(m[1], n[1]);
		spy.linkBackward(m[1], n[1]);
		self = spy.createCol(s[0], 3, 15);
		spy.testAddNodeAfter(self, m[1], p[1]);
		assertSame(null, m[1].getPrev());
		assertSame(p[1], m[1].getNext());
		assertSame(m[1], p[1].getPrev());
		assertSame(n[1], p[1].getNext());
		assertSame(p[1], n[1].getPrev());
		assertSame(null, n[1].getNext());		
	}
	
	public void testA3() {
		spy.linkForward(s[0], n[3], n[2], n[1], s[0]);
		spy.linkBackward(s[0], n[3], n[2], n[1], s[0]);
		self = spy.createCol(s[0], 3, 15);
		spy.testAddNodeAfter(self, s[0], n[4]);
		assertSame(n[1], s[0].getPrev());
		assertSame(n[4], s[0].getNext());
		assertSame(s[0], n[4].getPrev());
		assertSame(n[3], n[4].getNext());
		assertSame(n[4], n[3].getPrev());
		assertSame(n[2], n[3].getNext());
		assertSame(n[3], n[2].getPrev());
		assertSame(n[1], n[2].getNext());
		assertSame(n[2], n[1].getPrev());
		assertSame(s[0], n[1].getNext());	
	}
	
	public void testA4() {
		spy.linkForward(s[0], s[0]);
		spy.linkBackward(s[0], s[0]);
		spy.linkForward(m[1], m[2], m[3], m[4]);
		spy.linkBackward(m[1], m[2], m[3], m[4]);
		self = spy.createCol(s[0], 3, 15);
		spy.testAddNodeAfter(self, m[2], n[2]);
		assertSame(null, m[1].getPrev());
		assertSame(m[2], m[1].getNext());
		assertSame(m[1], m[2].getPrev());
		assertSame(n[2], m[2].getNext());
		assertSame(m[2], n[2].getPrev());
		assertSame(m[3], n[2].getNext());
		assertSame(n[2], m[3].getPrev());
		assertSame(m[4], m[3].getNext());
		assertSame(m[3], m[4].getPrev());
		assertSame(null, m[4].getNext());
	}

	
	/// testCx: testing "count"
	
	public void testC0() {
		spy.linkForward(s[0], s[0]);
		spy.linkBackward(s[0], s[0]);
		self = spy.createCol(s[0], 3, 15);
		assertEquals(0, spy.testCount(self));
	}
	
	public void testC4() {
		spy.linkForward(s[0], m[1], n[1], p[1], m[2], s[0]);
		spy.linkBackward(s[0], m[1], n[1], p[1], m[2], s[0]);
		self = spy.createCol(s[0], 3, 15);
		assertEquals(4, spy.testCount(self));
	}
	

	/// testRx: testing removeNode
	
	public void testR2() {
		spy.linkForward(s[0], m[1], s[0]);
		spy.linkBackward(s[0], m[1], s[0]);
		self = spy.createCol(s[0], 3, 15);
		spy.testRemoveNode(self, m[1]);
		assertSame(s[0], s[0].getPrev());
		assertSame(s[0], s[0].getNext());		
	}
	
	public void testR3() {
		spy.linkForward(s[0], s[0]);
		spy.linkBackward(s[0], s[0]);
		spy.linkForward(m[1], n[1], p[1]);
		spy.linkBackward(m[1], n[1], p[1]);
		self = spy.createCol(s[0], 3, 15);
		spy.testRemoveNode(self, n[1]);
		assertSame(null, m[1].getPrev());
		assertSame(p[1], m[1].getNext());
		assertSame(m[1], p[1].getPrev());
		assertSame(null, p[1].getNext());		
	}
	
	public void testR5() {
		spy.linkForward(s[0], m[1], m[2], m[3], m[4], m[5], s[0]);
		spy.linkBackward(s[0], m[1], m[2], m[3], m[4], m[5], s[0]);
		self = spy.createCol(s[0], 3, 15);
		spy.testRemoveNode(self, m[3]);
		assertSame(m[5], s[0].getPrev());
		assertSame(m[1], s[0].getNext());
		assertSame(s[0], m[1].getPrev());
		assertSame(m[2], m[1].getNext());
		assertSame(m[1], m[2].getPrev());
		assertSame(m[4], m[2].getNext());
		assertSame(m[2], m[4].getPrev());
		assertSame(m[5], m[4].getNext());
		assertSame(m[4], m[5].getPrev());
		assertSame(s[0], m[5].getNext());
	}
}
