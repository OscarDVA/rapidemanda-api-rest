# Imágenes para el Encabezado del PDF

Este directorio contiene las imágenes utilizadas en el encabezado del reporte PDF de demandas.

## Archivos Actuales

### Placeholders (Temporales)
- `logo-poder-judicial.svg` - Logo placeholder del Poder Judicial
- `logo-etiinlpt.svg` - Logo placeholder de ETIINLPT

## Instrucciones para Reemplazar con Imágenes Reales

Para usar las imágenes reales del diseño:

1. **Logo del Poder Judicial (izquierda)**
   - Reemplazar: `logo-poder-judicial.svg`
   - Formato recomendado: SVG, PNG o JPG
   - Tamaño recomendado: 60x60 píxeles
   - Ubicación en el PDF: Esquina superior izquierda

2. **Logo ETIINLPT (derecha)**
   - Reemplazar: `logo-etiinlpt.svg`
   - Formato recomendado: SVG, PNG o JPG
   - Tamaño recomendado: 60x60 píxeles
   - Ubicación en el PDF: Esquina superior derecha

## Formatos Soportados

El sistema soporta los siguientes formatos de imagen:
- SVG (recomendado para escalabilidad)
- PNG (recomendado para logos con transparencia)
- JPG/JPEG (para fotografías)

## Notas Técnicas

- Las imágenes se cargan desde el classpath usando `getClass().getClassLoader().getResourceAsStream()`
- Si una imagen no se puede cargar, se muestra un texto alternativo
- El sistema redimensiona automáticamente las imágenes a 60x60 píxeles
- Los logos se centran verticalmente en sus celdas respectivas

## Diseño del Encabezado

```
┌─────────────┬─────────────────────────────────────┬─────────────┐
│ Logo PJ     │     Corte Superior de Justicia      │ Logo        │
│ (60x60)     │         de Junín                    │ ETIINLPT    │
│             │   Comisión de Gestión de Despacho   │ (60x60)     │
└─────────────┴─────────────────────────────────────┴─────────────┘
┌─────────────────────────────────────────────────────────────────┐
│        DEMANDA LABORAL ANTE EL JUZGADO DE PAZ LETRADO          │
│                      (HASTA 70 URP)                            │
└─────────────────────────────────────────────────────────────────┘
```