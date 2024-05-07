import { Component,inject,OnInit } from '@angular/core';
import {Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClient,HttpClientModule} from '@angular/common/http';
import { FormsModule, NgModel } from '@angular/forms';
import { LoginComponent } from './components/login/login.component';
import { CookieService } from 'ngx-cookie-service';
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule,RouterOutlet,HttpClientModule,FormsModule,LoginComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})

export class AppComponent implements OnInit{
  title = 'ProiectFrontend';
  constructor(private router:Router,private cookieService:CookieService) {}
  ngOnInit() {
    if(this.cookieService.get('token')=="")
      this.router.navigate(['/login']);
    
  }
}