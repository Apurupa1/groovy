import org.example.CSVParser
import org.example.ClarityApiClient
import org.example.Department
import org.example.HierarchyBuilder

class Main {
    static void main(String[] args) {
        try {
            String csvFile = "test.csv" // File name in resources
            String clarityApiUrl = "https://clarity-instance.com/api/departments"
            String token = "your_access_token"

            // Step 1: Parse CSV
            Map<String, Department> departmentMap = CSVParser.parse(csvFile)

            if (departmentMap.isEmpty()) {
                println "No data found. Exiting."
                return
            }

            // Step 2: Build hierarchy
            Department root = HierarchyBuilder.build(departmentMap)

            if (!root) {
                println "Error: Could not determine root department."
                return
            }

            // Step 3: Print hierarchy for verification
            printHierarchy(root, 0)

            // Step 4: Send hierarchy to Clarity
            ClarityApiClient.sendToClarity(clarityApiUrl, token, root)

        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    // Utility method to print hierarchy
    static void printHierarchy(Department department, int level) {
        if (!department) return
        println "${'  ' * level}- ${department.name}"
        department.subDepartments.each { sub ->
            printHierarchy(sub, level + 1)
        }
    }
}