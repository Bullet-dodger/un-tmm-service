/** Response from GET /api/materials and GET /api/materials/search */
export interface MaterialSearchResponse {
  id: number;
  formula: string;
  displayName: string;
  tMin: number | null;
  tMax: number | null;
}

/** Spring Data Page wrapper */
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

/** Request body for POST /api/combustion/calculate */
export interface MaterialQuantityDto {
  materialId: number;
  moleCount: number;
}

export interface CombustionCalculationRequest {
  reagents: MaterialQuantityDto[];
  products: MaterialQuantityDto[];
}

/** Response from POST /api/combustion/calculate */
export interface CombustionCalculationResponse {
  adiabaticTemperature: number;
  reactionEnthalpy: number;
}

/** API error response */
export interface ApiError {
  status: number;
  error: string;
  message: string;
}
