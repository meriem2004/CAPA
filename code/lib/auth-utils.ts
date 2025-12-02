// Role mapping between backend and frontend
export type BackendRole = 'MANAGER' | 'OPERATOR' | 'QUALITY';
export type FrontendRole = 'declarant' | 'qualite' | 'direction';

/**
 * Maps backend role to frontend role
 */
export function mapBackendRoleToFrontend(backendRole: BackendRole): FrontendRole {
  const roleMap: Record<BackendRole, FrontendRole> = {
    OPERATOR: 'declarant',
    QUALITY: 'qualite',
    MANAGER: 'direction',
  };
  return roleMap[backendRole];
}

/**
 * Maps frontend role to backend role
 */
export function mapFrontendRoleToBackend(frontendRole: FrontendRole): BackendRole {
  const roleMap: Record<FrontendRole, BackendRole> = {
    declarant: 'OPERATOR',
    qualite: 'QUALITY',
    direction: 'MANAGER',
  };
  return roleMap[frontendRole];
}

