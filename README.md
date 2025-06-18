# http-client
IntelliJ IDEA Community  下面 Http Client插件
功能见：https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html?Http_client_in__product__code_editor#converting-curl-requests

## API Interaction using .http files

This project uses `.http` files to define and execute HTTP requests, which is useful for testing and interacting with APIs. These files are compatible with the HTTP Client available in IntelliJ IDEA and other JetBrains IDEs.

For more information on the IntelliJ IDEA HTTP Client, see the [official JetBrains documentation](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html).

### Example Requests

The `requests.http` file in this project contains example requests:

1.  **GET Request:**
    -   Sends a GET request to `https://httpbin.org/get`.
    -   This is a simple way to test if an endpoint is reachable and to inspect its response.

2.  **POST Request:**
    -   Sends a POST request to `https://httpbin.org/post` with a JSON payload.
    -   This demonstrates how to send data to an endpoint.

To execute these requests if you have IntelliJ IDEA:
1. Open the `requests.http` file.
2. Click the green run icon next to the request you want to execute.
3. The response will be shown in the IDE.
