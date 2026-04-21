package com.agri.service;

import java.util.List;

public interface AiService {
    
    String chat(String message, String history);
    
    String chatWithContext(String message, String context);
}
