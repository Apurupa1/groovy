package org.example

import com.opencsv.CSVReader

class CSVParser {
    static Map<String, Department> parse(String csvFile) {
        // Use the CSVParser class to get the class loader
        URL resourceUrl = CSVParser.class.classLoader.getResource(csvFile)
        if (!resourceUrl) {
            println "Error: Resource file $csvFile not found in the classpath."
            return [:]
        }

        InputStream inputStream = resourceUrl.openStream()

        Map<String, Department> departmentMap = [:]

        new CSVReader(new InputStreamReader(inputStream)).withCloseable { csvReader ->
            csvReader.readNext() // Skip header
            csvReader.each { values ->
                String id = values[0]
                String name = values[1]
                String parentId = values[2].equalsIgnoreCase("null") ? null : values[2]
                departmentMap[id] = new Department(id, name, parentId)
            }
        }

        return departmentMap
    }
}
