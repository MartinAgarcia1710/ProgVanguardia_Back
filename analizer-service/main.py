from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from typing import List, Literal
import os
import google.generativeai as genai
import json
from dotenv import load_dotenv

# Cargar variables de entorno desde el archivo .env si existe
load_dotenv()

app = FastAPI(
    title="AI Code Analyzer API",
    description="Microservicio en Python (FastAPI) para auditar código enviado por estudiantes usando LLMs.",
    version="1.0.0"
)

# Configurar Gemini (o la API que se utilice)
API_KEY = os.getenv("GEMINI_API_KEY")
if API_KEY:
    genai.configure(api_key=API_KEY)

# ==========================================
# MODELOS DE DATOS (Pydantic)
# ==========================================

class AuditRequest(BaseModel):
    code_snippet: str = Field(..., description="El fragmento de código a auditar.")
    language: str = Field(..., description="El lenguaje de programación (ej: python, java, kotlin).")

class AuditDetail(BaseModel):
    severity: Literal['Critico', 'Advertencia', 'Sugerencia'] = Field(..., description="Severidad del hallazgo.")
    category: str = Field(..., description="Categoría del hallazgo (ej: Seguridad, Clean Code, Sintaxis, Performance, Bug).")
    title: str = Field(..., description="Título corto descriptivo.")
    description: str = Field(..., description="Resumen del problema detectado.")
    affected_lines: str = Field(..., description="Líneas específicas donde está el error, ej: '12,14'.")
    suggested_fix: str = Field(..., description="Fragmento de código optimizado o corregido.")
    pedagogical_explanation: str = Field(..., description="Desarrollo conceptual didáctico del porqué está mal o es ineficiente.")

class AuditResponse(BaseModel):
    score_general: int = Field(..., description="Puntaje de calidad global asignado por la IA (0 a 100).")
    findings: List[AuditDetail] = Field(..., description="Lista de hallazgos detectados en el código.")


# ==========================================
# LÓGICA DE NEGOCIO (LLM Prompting)
# ==========================================

SYSTEM_PROMPT = """Eres un Senior Software Engineer realizando una auditoría de código enviada por estudiantes universitarios.
Tu objetivo es analizar el código fuente y detectar:
- Errores de sintaxis
- Problemas de seguridad (ej: SQL Injection, credenciales hardcodeadas)
- Problemas de performance
- Problemas de clean code (malas prácticas, nombres de variables confusos, funciones largas)
- Bugs potenciales

Debes devolver un JSON estructurado con la siguiente estructura exacta:
{
    "score_general": 85, // Un puntaje general del 0 al 100 evaluando el código
    "findings": [
        {
            "severity": "Critico" | "Advertencia" | "Sugerencia",
            "category": "Seguridad" | "Clean Code" | "Sintaxis" | "Performance" | "Bug",
            "title": "Título corto y descriptivo",
            "description": "Resumen del problema detectado",
            "affected_lines": "Líneas afectadas, ej: '12,14' o '5'",
            "suggested_fix": "Código optimizado o corregido",
            "pedagogical_explanation": "Explicación didáctica y respetuosa del porqué está mal o es ineficiente, orientada a que el alumno aprenda."
        }
    ]
}
Si el código está perfecto, el score_general debe ser 100 y findings debe ser una lista vacía [].
Devuelve ÚNICAMENTE un JSON válido, sin bloques de código Markdown ni texto adicional.
"""

@app.post("/api/audit", response_model=AuditResponse)
async def audit_code(request: AuditRequest):
    """
    Endpoint principal que recibe el código desde el orquestador en Java,
    lo envía al modelo de IA y devuelve los hallazgos en formato JSON.
    """
    if not API_KEY:
        raise HTTPException(status_code=500, detail="GEMINI_API_KEY no está configurada en las variables de entorno del servidor.")
    
    # Construcción del prompt del usuario (el código del alumno)
    prompt = f"Analiza el siguiente código escrito en {request.language}:\n\n```{request.language}\n{request.code_snippet}\n```"
    
    try:
        # Instanciar el modelo Gemini
        model = genai.GenerativeModel('gemini-2.5-flash', system_instruction=SYSTEM_PROMPT)
        
        # Generar contenido pidiendo explícitamente JSON
        response = model.generate_content(
            prompt,
            generation_config=genai.GenerationConfig(
                response_mime_type="application/json"
            )
        )
        
        # Parsear la respuesta y retornarla. FastAPI se encargará de validarla contra AuditResponse
        result_json = json.loads(response.text)
        return result_json
        
    except json.JSONDecodeError:
        raise HTTPException(status_code=500, detail="La IA no devolvió un JSON válido.")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al comunicarse con la IA: {str(e)}")

@app.get("/")
async def root():
    return {"message": "Microservicio de Analizador de IA en Python funcionando correctamente. POST a /api/audit para auditar."}
