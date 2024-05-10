import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';
@Injectable({
    providedIn: 'root',
  })
export class FriendsService {
    private apiKey = 'http://localhost:8080/api/friends';
    constructor(private http: HttpClient,private cookieService:CookieService) {}
    add(info:any): Observable<any> {
        return this.http.post<any>(`${this.apiKey}/add`, info);
    }
    delete(info:any): Observable<any> {
        return this.http.post<any>(`${this.apiKey}/delete`, info);
    }
    response(info:any): Observable<any> {
        console.log(info);
        return this.http.post<any>(`${this.apiKey}/response`, info);
    }
    getFriends(username:string): Observable<any> {
        return this.http.get<any>(`${this.apiKey}/get/${username}`);
    }
    sentRequests(username:string): Observable<any> {
        return this.http.get<any>(`${this.apiKey}/sent/${username}`);
    }
}