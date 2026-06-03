import requests
import json

# URL de nuestro microservicio local
url = "http://localhost:8000/api/audit"

# Simulamos lo que nos mandaría Java
payload = {
    "code_snippet": "def sumar_lista(numeros):\n  total = 0\n  for i in range(len(numeros)):\n    total = total + numeros[i]\n  return total",
    "language": "python"
}

print("Enviando código a auditar...")
response = requests.post(url, json=payload)

# Imprimimos la respuesta estructurada de la IA
print("\nRespuesta de la IA (JSON):\n")
print(json.dumps(response.json(), indent=2, ensure_ascii=False))
