import axios from 'axios';
import type {
  CombustionCalculationRequest,
  CombustionCalculationResponse,
  MaterialSearchResponse,
  Page,
} from './types';

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

/** GET /api/materials?page=&size=&sort= */
export async function getMaterials(
  page = 0,
  size = 20,
): Promise<Page<MaterialSearchResponse>> {
  const { data } = await api.get<Page<MaterialSearchResponse>>('/materials', {
    params: { page, size },
  });
  return data;
}

/** GET /api/materials/search?query=&page=&size= */
export async function searchMaterials(
  query: string,
  page = 0,
  size = 20,
): Promise<Page<MaterialSearchResponse>> {
  const { data } = await api.get<Page<MaterialSearchResponse>>(
    '/materials/search',
    { params: { query, page, size } },
  );
  return data;
}

/** GET /api/materials/:id */
export async function getMaterial(
  id: number,
): Promise<MaterialSearchResponse> {
  const { data } = await api.get<MaterialSearchResponse>(`/materials/${id}`);
  return data;
}

/** POST /api/combustion/calculate */
export async function calculateCombustion(
  request: CombustionCalculationRequest,
): Promise<CombustionCalculationResponse> {
  const { data } = await api.post<CombustionCalculationResponse>(
    '/combustion/calculate',
    request,
  );
  return data;
}
