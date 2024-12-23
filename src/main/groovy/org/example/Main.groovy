@Grab('com.opencsv:opencsv:5.7.1')

import com.opencsv.CSVReader
import java.net.HttpURLConnection
import java.net.URL

class DepartmentHierarchy {
    static class Department {
        String id
        String name
        String parentId
        List<Department> subDepartments = []

        Department(String id, String name, String parentId) {
            this.id = id
            this.name = name
            this.parentId = parentId
        }

        @Override
        String toString() {
            return "Department{id='$id', name='$name', parentId='$parentId', subDepartments=$subDepartments}"
        }
    }

    static void main(String[] args) {
        String csvFile = "test.csv" // File name in resources
        Map<String, Department> departmentMap = [:]
        Department root = null

        // Load the CSV file
        InputStream inputStream = DepartmentHierarchy.class.classLoader.getResourceAsStream(csvFile)
        if (!inputStream) {
            println "Error: Resource file $csvFile not found in the classpath."
            return
        }

        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))
            csvReader.readNext() // Skip header

            // Read departments into a map
            csvReader.each { values ->
                String id = values[0]
                String name = values[1]
                String parentId = values[2].equalsIgnoreCase("null") ? null : values[2]
                departmentMap[id] = new Department(id, name, parentId)
            }

            // Build the hierarchy
            departmentMap.values().each { department ->
                if (!department.parentId) {
                    root = department
                } else {
                    Department parent = departmentMap[department.parentId]
                    parent?.subDepartments << department
                }
            }

            // Print hierarchy for verification
            printHierarchy(root, 0)

            // Send hierarchy to Clarity PPM
            sendHierarchyToClarity(root)

        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    private static void printHierarchy(Department department, int level) {
        if (!department) return

        println "${'  ' * level}${department.name}"
        department.subDepartments.each { sub ->
            printHierarchy(sub, level + 1)
        }
    }

    private static void sendHierarchyToClarity(Department department) {
        if (!department) return

        // Base URL for Clarity API (update with your Clarity instance)
        String clarityApiUrl = "https://clarity-instance.com/api/departments"

        try {
            URL url = new URL(clarityApiUrl)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection()
            connection.setRequestMethod("POST")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Authorization", "Bearer your_access_token")
            connection.setDoOutput(true)

            // Prepare JSON payload
            String payload = """
            {
                "id": "${department.id}",
                "name": "${department.name}",
                "parentId": "${department.parentId ?: ""}"
            }
            """

            // Send payload
            connection.outputStream.withWriter("UTF-8") { writer ->
                writer.write(payload)
            }

            // Check response
            int responseCode = connection.responseCode
            if (responseCode == 201) {
                println "Successfully created department: ${department.name}"
            } else {
                println "Failed to create department: ${department.name}. Response code: $responseCode"
                connection.inputStream?.withReader { reader ->
                    println reader.text
                }
            }

            // Close the connection
            connection.disconnect()

        } catch (Exception e) {
            println "Error sending department ${department.name} to Clarity: ${e.message}"
        }

        // Recursively send sub-departments
        department.subDepartments.each { sub ->
            sendHierarchyToClarity(sub)
        }
    }
}
