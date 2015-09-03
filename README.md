# Introduction

This is a library that allows you to easily test beans for
       
* getters and setters correctness
* equals and hashcode correctness
* toString correctness

This library has been inspired by so many other projects (JavaBeanTester, GetterSetterVerifier to mention a few). So thanks all for the inspiration.

The library is also inspired by the realization of the importance of having easy ways to test boilerplate code.

# Examples

          
         
             @Test
             public void testRunAllTestsWorks() {
                 EqualAndHashcodeTester
                     .forClass(Bean.class)
                     .verify();
             }
             
             @Test
             public void testRunAllTestsWorks() {
                 EqualAndHashcodeTester
                     .forClass(Bean.class, () -> new Bean(System.timeInMillis())
                     .verify();
             }