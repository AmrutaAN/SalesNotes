import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, UserRequest, UserRole } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/users';

  getUsers(search?: string, role?: UserRole, active?: boolean): Observable<User[]> {
    let params = new HttpParams();
    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }
    if (role) {
      params = params.set('role', role);
    }
    if (active !== undefined && active !== null) {
      params = params.set('active', active.toString());
    }
    return this.http.get<User[]>(this.apiUrl, { params });
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  createUser(request: UserRequest): Observable<User> {
    return this.http.post<User>(this.apiUrl, request);
  }

  updateUser(id: number, request: UserRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, request);
  }

  updateStatus(id: number, active: boolean): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/${id}/status`, { active });
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
