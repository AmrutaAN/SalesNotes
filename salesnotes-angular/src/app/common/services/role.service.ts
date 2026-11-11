import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RoleItem } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/roles';

  getRoles(): Observable<RoleItem[]> {
    return this.http.get<RoleItem[]>(this.apiUrl);
  }

  getRoleById(id: string): Observable<RoleItem> {
    return this.http.get<RoleItem>(`${this.apiUrl}/${id}`);
  }

  createRole(role: { code: string; name: string; description?: string }): Observable<RoleItem> {
    return this.http.post<RoleItem>(this.apiUrl, role);
  }

  updateRole(id: string, role: { code: string; name: string; description?: string }): Observable<RoleItem> {
    return this.http.put<RoleItem>(`${this.apiUrl}/${id}`, role);
  }

  deleteRole(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
