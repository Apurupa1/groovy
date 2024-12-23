package org.example

@Grab('com.opencsv:opencsv:5.7.1')

import com.opencsv.CSVReader

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

            // Print hierarchy (for testing)
            printHierarchy(root, 0)

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
}
