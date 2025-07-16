import logging


def configure_logging(app):
    log_level = getattr(logging, app.config.get('LOG_LEVEL', 'INFO').upper())
    
    logging.basicConfig(
        level=log_level,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    )
    
    app.logger.setLevel(log_level) 