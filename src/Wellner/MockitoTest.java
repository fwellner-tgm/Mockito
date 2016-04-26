package Wellner;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.exceptions.verification.NoInteractionsWanted;

import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations.*;

import java.util.LinkedList;
import java.util.List;
import static org.mockito.Matchers.*;

/**
 * @author Florian Wellner
 */

public class MockitoTest {

	List mockedList;
	List mockOne;
	List mockTwo;
	List mockThree;
	

	@Before
	public void setUp() throws Exception {
		mockedList = mock(List.class);
		mockOne = mock(List.class);
		mockTwo = mock(List.class);
		mockThree = mock(List.class);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testBehaviour() {

		// mock creation
		// In der Liste mockedList "one" einfügen
		mockedList.add("one");
		// MockedList clearen
		mockedList.clear();

		// verify...überprüft ob die Aktion auf durchgeführt wurde
		verify(mockedList).add("one");
		verify(mockedList).clear();
	}

	@Test(expected = RuntimeException.class)
	public void testStubbing() {
		// You can mock concrete classes, not just interfaces
		// Die ganze Klasse ist "gemocked"
		LinkedList mockedList = mock(LinkedList.class);

		// Wenn ein Element in mockedList an der Stelle 1 (Index 0) vorhanden
		// ist, wird "first" returned
		when(mockedList.get(0)).thenReturn("first");
		// Wenn ein Element in mockedList an der Stelle 2 (Index 0) vorhanden
		// ist, wird eine Exception gethrowed
		when(mockedList.get(1)).thenThrow(new RuntimeException());

		// following prints "first"
		System.out.println(mockedList.get(0));

		// following throws runtime exception
		System.out.println(mockedList.get(1));

		// following prints "null" because get(999) was not stubbed
		System.out.println(mockedList.get(999));

		// Although it is possible to verify a stubbed invocation, usually it's
		// just redundant
		// If your code cares what get(0) returns, then something else breaks
		// (often even before verify() gets executed).
		// If your code doesn't care what get(0) returns, then it should not be
		// stubbed. Not convinced? See here.
		verify(mockedList).get(0);
	}

	@Test
	public void testArgumentMatcher() {

		// stubbing using built-in anyInt() argument matcher
		/* Wenn an irgendeiner Stelle in mockedList etwas vorhanden ist (außer null), dann wird "element" zurückgeworfen */
		when(mockedList.get(anyInt())).thenReturn("element");

		// stubbing using custom matcher (let's say isValid() returns your own
		// matcher implementation):
		// when(mockedList.contains(argThat())).thenReturn("element");

		// following prints "element"
		System.out.println(mockedList.get(999));

		// you can also verify using an argument matcher
		verify(mockedList).get(anyInt());
	}

	@Test
	public void testNumberOfInvocation() {
		// using mock
		mockedList.add("once");

		mockedList.add("twice");
		mockedList.add("twice");

		mockedList.add("three times");
		mockedList.add("three times");
		mockedList.add("three times");

		// following two verifications work exactly the same - times(1) is used
		// by default
		verify(mockedList).add("once");
		// Es wird überprüft, ob EIN MAL "once" eingefügt wurde
		verify(mockedList, times(1)).add("once");

		// exact number of invocations verification
		verify(mockedList, times(2)).add("twice");
		verify(mockedList, times(3)).add("three times");

		// verification using never(). never() is an alias to times(0)
		// Überprüft ob "never happended" nie hinzugefügt wurde
		verify(mockedList, never()).add("never happened");

		// verification using atLeast()/atMost()
		// Überprüft ob mindestens ein mal "three times" hinzugefügt wurde
		verify(mockedList, atLeastOnce()).add("three times");
		// Überprüft ob mindestens zwei mal "twice" hinzugefügt wurde
		verify(mockedList, atLeast(2)).add("twice");
		// Überprüft ob maximal fünf mal "three times" hinzugefügt wurdeF
		verify(mockedList, atMost(5)).add("three times");
	}

	@Test(expected = RuntimeException.class)
	public void testStubbingWithException() {

		// Wirft RuntimeException, wenn mockedList leer ist
		doThrow(new RuntimeException()).when(mockedList).clear();

		// mockedList wird geleert
		mockedList.clear();
	}

	@Test
	public void testVerificationInOrder() {
		// A. Single mock whose methods must be invoked in a particular order
		List singleMock = mock(List.class);

		// using a single mock
		singleMock.add("was added first");
		singleMock.add("was added second");

		// create an inOrder verifier for a single mock
		InOrder inOrder = inOrder(singleMock);

		// following will make sure that add is first called with "was added
		// first, then with "was added second"
		inOrder.verify(singleMock).add("was added first");
		inOrder.verify(singleMock).add("was added second");

		// B. Multiple mocks that must be used in a particular order
		List firstMock = mock(List.class);
		List secondMock = mock(List.class);

		// using mocks
		firstMock.add("was called first");
		secondMock.add("was called second");

		// create inOrder object passing any mocks that need to be verified in
		// order
		inOrder = inOrder(firstMock, secondMock);

		// following will make sure that firstMock was called before secondMock
		inOrder.verify(firstMock).add("was called first");
		inOrder.verify(secondMock).add("was called second");

		// Oh, and A + B can be mixed together at will
	}
	
	@Test
	public void testNeverHappened() {
		 //using mocks - only mockOne is interacted
		 mockOne.add("one");

		 //ordinary verification
		 verify(mockOne).add("one");

		 //verify that method was never called on a mock
		 //Überprüfen, dass "two" nicht gecalled wurde
		 verify(mockOne, never()).add("two");

		 //verify that other mocks were not interacted
		 //Verifizieren, dass die anderen zwei mocks nicht
		 //benutzt worden sind
		 verifyZeroInteractions(mockTwo, mockThree);

	}
	
	@Test(expected = NoInteractionsWanted.class)
	public void testFindingRedundantInovocations() {
		 //using mocks
		 mockedList.add("one");
		 mockedList.add("two");

		 verify(mockedList).add("one");

		 //following verification will fail
		 verifyNoMoreInteractions(mockedList);
	}
	
//	public class ArticleManagerTest extends SampleBaseTestCase { 
//	     
//	       @Mock private ArticleCalculator calculator;
//	       @Mock private ArticleDatabase database;
//	       @Mock private UserProvider userProvider;
//	     
//	       private ArticleManager manager;
//	     
//	       @Before public void setup() {
//	           manager = new ArticleManager(userProvider, database, calculator);
//	       }
//	   }
//	   
//	   public class SampleBaseTestCase {
//	   
//	       @Before public void initMocks() {
//	           MockitoAnnotations.initMocks(this);
//	       }
//	   }

	@Test(expected = RuntimeException.class)
	public void testStubbingConsecutiveCalls() {
//		when(mock.someMethod("some arg"))
//		   .thenThrow(new RuntimeException())
//		   .thenReturn("foo");
		when(mockedList.add("some arg"))
		   .thenThrow(new RuntimeException())
		   .thenReturn(true);

		 //First call: throws runtime exception:
		 //mock.someMethod("some arg");
		 mockedList.add("some arg");


		 //Second call: prints true
		 //System.out.println(mock.someMethod("some arg"));
		 System.out.println(mockedList.add("some arg"));

		 //Any consecutive call: prints true as well (last stubbing wins).
		 //System.out.println(mock.someMethod("some arg"));
		 System.out.println(mockedList.add("some arg"));
	}
}


