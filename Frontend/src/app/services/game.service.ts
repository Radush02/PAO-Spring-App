import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';
@Injectable({
    providedIn: 'root',
  })
  
export class GameService {
    private apiKey = 'http://localhost:8080/api/game';
    constructor(private http: HttpClient,private cookieService:CookieService) {}
    getGames(username: string): Observable<any> {
        return this.http.get<any>(`${this.apiKey}/displayMultiplayerGame/`+username);
    }
}
