# Laboratorio8 - Integración de Room en la Galería (Entrega completa)

## Resumen
Esta versión implementa las funcionalidades requeridas por el lab: cache por query, favoritos persistentes, búsquedas recientes, paginación con Paging 3 + RemoteMediator, y lectura offline básica.

### Consideraciones
- El cliente de red usa Retrofit con modelos para Unsplash. Añade tu API key en la construcción del cliente o como `client_id` en las llamadas.
- Base de datos Room con entidades: `PhotoEntity`, `RecentQuery`, `RemoteKeys`.
- `PhotoRepository` combina RemoteMediator + Room para cache paginado por query.
- `HomeViewModel` expone un `Flow<PagingData<PhotoEntity>>` y guarda búsquedas recientes.
- `DetailsViewModel` lee por ID desde Room y ofrece `toggleFavorite()`.

## Cómo usar / compilar
1. Abre el proyecto en Android Studio.
2. En el `AppModule` agrega la dependencia de Retrofit + Moshi/Gson si aún no están en tu `libs.versions.toml` o `build.gradle.kts`.
3. Proporciona la `clientId` de Unsplash o Pexels en la construcción del `PhotoApi` (puede ser pasada al `PhotoRepository`).
4. Sync y Build. Ejecuta en emulador/dispositivo. Para validar el modo offline, ejecuta búsqueda con red, luego mata la app y desactiva la red al relanzar.

## Respuestas a las preguntas de reflexión (incluidas en README)

### 1) ¿Cómo invalidarías cache por caducidad (updatedAt) o cambio de parámetros?
- Estrategia propuesta:
  - Guardar `updatedAt` por entidad (ya incluido en `PhotoEntity`).
  - Al iniciar una búsqueda, comprobar la fecha del último `updatedAt` para esa `queryKey`. Si es más viejo que un umbral (p.ej. 24h), forzar `RemoteMediator` con `LoadType.REFRESH` y reemplazar cache.
  - También exponer una operación manual de invalidación por usuario (pull-to-refresh) que borre `photos` y `remote_keys` para esa query.
  - Para cambios de parámetros (por ejemplo filtros), usar `queryKey` normalizada que incluya parámetros serializados; así cada combinación de parámetros mapea a su propia cache.

### 2) ¿Qué conflictos surgen entre cache parcial y paginado? ¿Cómo resolverlos?
- Problemas comunes:
  - **Duplicados**: cuando páginas vienen mezcladas entre cache y red. Solución: usar `OnConflictStrategy.REPLACE` y claves de orden (pageIndex, id) para ordenar consistentemente.
  - **Inconsistencia**: datos parciales viejos al mezclar con nuevas páginas. Solución: en `REFRESH` limpiar la cache y remote keys antes de insertar la nueva página.
  - **Gaps**: páginas faltantes si la app falla entre inserciones. Solución: hacer inserciones dentro de una transacción (Room `withTransaction`) y registrar `RemoteKeys` solo después de insertar.

### 3) ¿Cuándo introducir ViewModel/Repository/RemoteMediator formalmente en un proyecto grande?
- Indicadores para escalar:
  - Si múltiples pantallas consumen la misma fuente de datos (necesitas un `Repository` centralizado para consistencia).
  - Si la lógica de paginado/criterios de cache se vuelve compleja — RemoteMediator + RemoteKeys ayuda a mantener la consistencia y reintentos automáticos.
  - Para pruebas: separar la lógica en `Repository` y usar `ViewModel` para exponer `State/Flow` facilita Mockito/Coroutines testing.
  - En equipos, una capa `Repository` es esencial para separar responsabilidades (UI vs Data).

## Notas finales
- Esta entrega crea la base de datos y la infraestructura necesaria para cumplir los criterios del lab. Aun así, faltan los ajustes UI para vincular el `Pager` a `LazyVerticalGrid` en Compose, y pasar instancias concretas (inyección DI o Singletons) del `AppDatabase`, `PhotoApi` y `PhotoRepository` en el `MainActivity`/`Application`.
- Si quieres, puedo:
  1. Añadir la inyección manual en `MainActivity` (con Room.databaseBuilder y Retrofit builder) y un adaptador `PagingData` -> `LazyVerticalGrid` con Coil ya integrado. 
  2. Alternativamente usar Hilt para inyección (requiere cambios en gradle).

---
Entrega preparada por el asistente. Si deseas que continúe y deje TODO integrado y funcionando en UI (binding del Pager a la grilla, detalles, share, pruebas DAO y video-demo checklist), indicar y lo hago en la siguiente iteración.
