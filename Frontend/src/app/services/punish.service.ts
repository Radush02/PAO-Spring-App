import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root',
})

export class PunishService {
  private apiKey = 'http://localhost:8080/api/punish';
  constructor(private http: HttpClient,private cookieService:CookieService) {}
    ban(info: any): Observable<any> {
        return this.http.post<any>(`${this.apiKey}/ban`, info);
    }
    unban(info: any): Observable<any> {
        return this.http.post<any>(`${this.apiKey}/unban`, info);
    }
    mute(info: any): Observable<any> {
        return this.http.post<any>(`${this.apiKey}/mute`, info);
    }
    unmute(info: any): Observable<any> {
        return this.http.post<any>(`${this.apiKey}/unmute`, info);
    }
    warn(info: any): Observable<any> {
        return this.http.post<any>(`${this.apiKey}/warn`, info);
    }
    getLogs(admin:string): Observable<any> {
        return this.http.get<any>(`${this.apiKey}/getLogs?admin=${admin}`,{responseType: 'blob' as 'json',      observe: 'response'});
    }
    assignRole(info:any): Observable<any> {
        return this.http.post<any>(`${this.apiKey}/assignRole`,info);
    }
    revertAction(info:any): Observable<any> {
        return this.http.post<any>(`${this.apiKey}/revert`,info);
    }
}

