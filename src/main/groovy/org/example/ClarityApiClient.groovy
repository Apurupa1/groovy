package org.example

class ClarityApiClient {
    static void sendToClarity(String apiUrl, String token, Department department) {
        if (!department) return

        try {
            URL url = new URL(apiUrl)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection()
            connection.setRequestMethod("POST")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $token")
            connection.setDoOutput(true)

            String payload = """
            {
                "id": "${department.id}",
                "name": "${department.name}",
                "parentId": "${department.parentId ?: ""}"
            }
            """

            connection.outputStream.withWriter("UTF-8") { writer ->
                writer.write(payload)
            }

            int responseCode = connection.responseCode
            if (responseCode == 201) {
                println "Successfully created department: ${department.name}"
            } else {
                println "Failed to create department: ${department.name}. Response code: $responseCode"
                connection.inputStream?.withReader { reader ->
                    println reader.text
                }
            }
            connection.disconnect()

        } catch (Exception e) {
            println "Error sending department ${department.name} to Clarity: ${e.message}"
        }

        department.subDepartments.each { sub ->
            sendToClarity(apiUrl, token, sub)
        }
    }
}