# Network Routing System

## Overview
This project implements a network system that finds the best route between different hosts (computers), considering factors like bandwidth and latency to optimize communication paths.

## What It Does
The system takes a network of connected hosts and determines the most efficient path for data to travel from one host to another. It balances multiple factors:
- **Bandwidth**: How much data can be transmitted
- **Latency**: How fast the data travels
- **Path efficiency**: Finding the quickest route

## Key Components

### Core Classes
- **Network**: Manages all the hosts and connections in the system
- **Host**: Represents individual computers/nodes in the network
- **PathFinder**: Uses Dijkstra's algorithm to calculate optimal routes
- **MinHeap**: Data structure that helps quickly find the best next path to explore
- **RouteState**: Tracks the current path being evaluated

### Data Structures
- **HashTable**: Custom implementation for fast lookups
- **Stack**: Used for processing data
- **MinHeap**: Priority queue for efficient pathfinding

## How It Works
1. The system receives requests to find a route between two hosts
2. PathFinder explores possible paths using Dijkstra's algorithm
3. It calculates the "cost" of each path based on bandwidth and latency
4. Returns the most efficient route

## Input/Output
- Reads commands from an input file
- Processes network operations (adding hosts, creating connections, finding paths)
- Outputs results to an output file

## Use Cases
- Finding efficient routes for data transmission in computer networks
- Optimizing network performance
- Determining best paths considering multiple constraints
