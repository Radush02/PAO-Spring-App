import { Component } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-lobbies',
  standalone: true,
  imports: [NavbarComponent,CommonModule,RouterOutlet,HttpClientModule,ReactiveFormsModule],
  templateUrl: './lobbies.component.html',
  styleUrl: './lobbies.component.css'
})
export class LobbiesComponent {
  constructor(private router:Router){
    
  }
}
