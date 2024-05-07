import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root',
})

export class UserService {
  private apiKey = 'http://localhost:8080/api/user';

  constructor(private http: HttpClient,private cookieService:CookieService) {}

  login(info: any): Observable<any> {
    return this.http.post<any>(`${this.apiKey}/login`, info);
  }

  register(info: any): Observable<any> {
    return this.http.post<any>(`${this.apiKey}/register`, info);
  }

  isLoggedIn(): string {
    const token=this.cookieService.get('token');
    if (token) {
      return token;
    }
    return "";
  }
  logout():any{
    this.cookieService.delete('token');
    return null;
  } 
}