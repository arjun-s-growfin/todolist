package com.example.todolist.service;

public class ThreadLocalExample {
    
    // Regular ThreadLocal
    private static ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);
    
    // InheritableThreadLocal - child threads inherit parent's value
    private static InheritableThreadLocal<UserDTO> inheritableThreadLocal = 
        new InheritableThreadLocal<>();
    
    static class UserDTO {
        String name;
        int age;
        
        UserDTO(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        @Override
        public String toString() {
            return "UserDTO{name='" + name + "', age=" + age + "}";
        }
    }
    
    public static void demonstrateInheritableThreadLocal() throws InterruptedException {
        // Parent thread creates a mutable DTO
        UserDTO dto = new UserDTO("Arjun", 21);
        inheritableThreadLocal.set(dto);
        
        System.out.println("Parent thread - Initial: " + inheritableThreadLocal.get());
        
        // Thread 1 - modifies the DTO
        Thread thread1 = new Thread(() -> {
            UserDTO childDto = inheritableThreadLocal.get();
            System.out.println("Thread 1 - Inherited: " + childDto);
            
            // Modify the mutable DTO
            childDto.name = "Modified by Thread 1";
            childDto.age = 99;
            
            System.out.println("Thread 1 - After modification: " + childDto);
        });
        
        thread1.start();
        thread1.join(); // Wait for thread1 to complete
        
        // Thread 2 - reads the DTO sequentially after thread1
        Thread thread2 = new Thread(() -> {
            UserDTO childDto = inheritableThreadLocal.get();
            System.out.println("Thread 2 - Inherited: " + childDto);
        });
        
        thread2.start();
        thread2.join();
        
        // Parent thread reads again
        System.out.println("Parent thread - After children: " + inheritableThreadLocal.get());
        
        inheritableThreadLocal.remove(); // Always clean up
    }
    
    public static void main(String[] args) throws InterruptedException {
        demonstrateInheritableThreadLocal();
    }
}