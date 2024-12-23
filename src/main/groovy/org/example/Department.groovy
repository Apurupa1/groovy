package org.example

@Grab('com.opencsv:opencsv:5.7.1')

// Define the Department class (data model)
class Department {
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

