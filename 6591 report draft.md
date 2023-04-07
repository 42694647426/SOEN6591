**Detecting Anti-patterns in Java Projects**

**Introduction**

Exception handling is a important feature that is usually separated from the standard source code, and extensively used to aid software comprehension and maintenance. (TBC)

**Background**

Modern programming languages, often provide funtionalities for handling exceptions. These functionalities are designed to be separated from the standard source code. This mechanism allows developers to gracefully handle errors and unexpected events that may occur during the execution, rather than letting the program crash or terminate abruptly. The handling of exceptions usually requires throw statements and try-catch-finally blocks. These features are employeed wildly in practice, and with this mechanism, developer can improve the robustness, as well as the reliability of the code. Hence this mechanism is leveraged extensively to aid software comprehension and maintenance.

Having recognized the benefits of using exception handling, it is worth noting that the misuse of this feature can potentially result in catastrophical failures. In practice, a significant amount of anti-patterns exist in subject systems, indicating that practitioners may lack a thorough understanding of exception handling. 

Therefore, we proposed a tool to find various types of exception handling anti-patterns, with which we will be able to identify three different types of anti-patterns: throws generic, kitchen sink and destructive wrapping.

**Approach**



**Results**

**Conclusion**



